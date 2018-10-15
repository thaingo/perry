package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.idm.service.authorization.UserRolesService.getStrongestAdminRole;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByEmail;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByRacfId;
import static gov.ca.cwds.service.messages.MessageCode.ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY;
import static gov.ca.cwds.service.messages.MessageCode.NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE;
import static gov.ca.cwds.service.messages.MessageCode.NO_USER_WITH_RACFID_IN_CWSCMS;
import static gov.ca.cwds.service.messages.MessageCode.USER_WITH_EMAIL_EXISTS_IN_IDM;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;
import static gov.ca.cwds.util.Utils.toUpperCase;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.authorization.AuthorizationService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import gov.ca.cwds.rest.api.domain.UserIdmValidationException;
import gov.ca.cwds.rest.api.domain.auth.GovernmentEntityType;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Profile("idm")
public class ValidationServiceImpl implements ValidationService {

  private UserByAdminRolesValidator userByAdminRolesValidator;

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidationServiceImpl.class);

  private MappingService mappingService;

  private CwsUserInfoService cwsUserInfoService;

  private MessagesService messages;

  private CognitoServiceFacade cognitoServiceFacade;

  private AuthorizationService authorizeService;

  @Override
  public User validateUserCreate(UniversalUserToken admin, User user) {
    String racfId = user.getRacfid();
    if (StringUtils.isNotBlank(racfId)) {
      return validateRacfidUserCreate(user);
    } else {
        return user;
    }
  }

  private User validateRacfidUserCreate(User user) {
    String racfId = user.getRacfid();
    String email = user.getEmail();

    CwsUserInfo cwsUser = cwsUserInfoService.getCwsUserByRacfId(racfId);
    if (cwsUser == null) {
      throwValidationException(NO_USER_WITH_RACFID_IN_CWSCMS, racfId);
    }

    if (checkIfUserWithEmailExistsInCognito(email)) {
      throwValidationException(USER_WITH_EMAIL_EXISTS_IN_IDM, email);
    }

    if (isActiveRacfIdPresent(racfId)) {
      throwValidationException(ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM, racfId);
    }

    enrichUserByCwsData(user, cwsUser);

    Optional<MessageCode> authorizationError = buildAuthorizationError();
    if (!authorizeService.canCreateUser(user) && authorizationError.isPresent()) {
      throwValidationException(authorizationError.get(), user.getCountyName());
    }
    return user;
  }

  @Override
  public void validateUpdateUser(UniversalUserToken admin, UserType existedCognitoUser, UserUpdate updateUserDto) {
    User newUser = getNewUser(existedCognitoUser, updateUserDto);
    userByAdminRolesValidator.validate(newUser);

    validateActivateUser(existedCognitoUser, updateUserDto);
  }

  private void enrichUserByCwsData(User user, CwsUserInfo cwsUser) {
    user.setRacfid(cwsUser.getRacfId());
    enrichDataFromCwsOffice(cwsUser.getCwsOffice(), user);
    enrichDataFromStaffPerson(cwsUser.getStaffPerson(), user);
  }

  private void enrichDataFromStaffPerson(StaffPerson staffPerson, final User user) {
    if (staffPerson != null) {
      user.setFirstName(staffPerson.getFirstName());
      user.setLastName(staffPerson.getLastName());
      user.setEndDate(staffPerson.getEndDate());
      user.setStartDate(staffPerson.getStartDate());
    }
  }

  private void enrichDataFromCwsOffice(CwsOffice office, final User user) {
    if (office != null) {
      user.setOfficeId(office.getOfficeId());
      Optional.ofNullable(office.getPrimaryPhoneNumber())
          .ifPresent(e -> user.setPhoneNumber(e.toString()));
      Optional.ofNullable(office.getPrimaryPhoneExtensionNumber())
          .ifPresent(user::setPhoneExtensionNumber);
      Optional.ofNullable(office.getGovernmentEntityType())
          .ifPresent(
              x -> user.setCountyName((GovernmentEntityType.findBySysId(x)).getDescription()));
    }
  }

  private Optional<MessageCode> buildAuthorizationError() {
    switch (getStrongestAdminRole((getCurrentUser()))) {
      case COUNTY_ADMIN:
        return Optional.of(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_COUNTY);
      case OFFICE_ADMIN:
        return Optional.of(NOT_AUTHORIZED_TO_ADD_USER_FOR_OTHER_OFFICE);
      default:
        return Optional.empty();
    }
  }

  private boolean isActiveRacfIdPresent(String racfId) {
    Collection<UserType> cognitoUsersByRacfId =
        cognitoServiceFacade.searchAllPages(composeToGetFirstPageByRacfId(toUpperCase(racfId)));
    return !CollectionUtils.isEmpty(cognitoUsersByRacfId)
        && isActiveUserPresent(cognitoUsersByRacfId);
  }

  private boolean checkIfUserWithEmailExistsInCognito(String email) {
    Collection<UserType> cognitoUsers =
        cognitoServiceFacade.searchPage(composeToGetFirstPageByEmail(email)).getUsers();
    return !CollectionUtils.isEmpty(cognitoUsers);
  }

  private void throwValidationException(MessageCode messageCode, Object... args) {
    String msg = messages.getTechMessage(messageCode, args);
    String userMsg = messages.getUserMessage(messageCode, args);
    LOGGER.error(msg);
    throw new UserIdmValidationException(msg, userMsg, messageCode);
  }

  private User getNewUser(UserType existedCognitoUser, UserUpdate updateUserDto) {
    User user = mappingService.toUser(existedCognitoUser);
    enrichUserByUpdateDto(user, updateUserDto);
    return user;
  }

  static void enrichUserByUpdateDto(User user, UserUpdate updateUserDto) {
    Boolean newEnabled = updateUserDto.getEnabled();
    if(newEnabled != null) {
      user.setEnabled(newEnabled);
    }
    Set<String> newPermissions = updateUserDto.getPermissions();
    if(newPermissions != null) {
      user.setPermissions(newPermissions);
    }
    Set<String> newRoles = updateUserDto.getRoles();
    if(newRoles != null) {
      user.setRoles(newRoles);
    }
  }

  private void validateActivateUser(UserType existedCognitoUser, UserUpdate updateUserDto) {
    if (!canChangeToEnableActiveStatus(updateUserDto.getEnabled(),
        existedCognitoUser.getEnabled())) {
      return;
    }
    String racfId = CognitoUtils.getRACFId(existedCognitoUser);
    if (StringUtils.isNotBlank(racfId)) {
      validateActivateUser(racfId);
    }
  }

  private static boolean canChangeToEnableActiveStatus(Boolean newEnabled, Boolean currentEnabled) {
    return newEnabled != null && !newEnabled.equals(currentEnabled) && newEnabled;
  }

  void validateActivateUser(String racfId) {
    CwsUserInfo cwsUser = cwsUserInfoService.getCwsUserByRacfId(racfId);
    // validates users not active in CWS cannot be set to Active in CWS CARES
    if (cwsUser == null) {
      String msg = messages.getTechMessage(NO_USER_WITH_RACFID_IN_CWSCMS, racfId);
      String userMsg = messages.getUserMessage(NO_USER_WITH_RACFID_IN_CWSCMS, racfId);
      throw new UserIdmValidationException(msg, userMsg, NO_USER_WITH_RACFID_IN_CWSCMS);
    }

    // validates no other Active users with same RACFID in CWS CARES exist
    if (isActiveRacfIdPresent(racfId)) {
      String msg = messages.getTechMessage(ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM, racfId);
      String userMsg = messages.getUserMessage(ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM, racfId);
      throw new UserIdmValidationException(msg, userMsg, ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM);
    }
  }

  private static boolean isActiveUserPresent(Collection<UserType> cognitoUsers) {
    return cognitoUsers
        .stream()
        .anyMatch(userType -> Objects.equals(userType.getEnabled(), Boolean.TRUE));
  }

  @Autowired
  public void setUserByAdminRolesValidator(
      UserByAdminRolesValidator userByAdminRolesValidator) {
    this.userByAdminRolesValidator = userByAdminRolesValidator;
  }

  @Autowired
  public void setMappingService(MappingService mappingService) {
    this.mappingService = mappingService;
  }

  @Autowired
  public void setCwsUserInfoService(CwsUserInfoService cwsUserInfoService) {
    this.cwsUserInfoService = cwsUserInfoService;
  }

  @Autowired
  public void setMessages(MessagesService messages) {
    this.messages = messages;
  }

  @Autowired
  public void setCognitoServiceFacade(
      CognitoServiceFacade cognitoServiceFacade) {
    this.cognitoServiceFacade = cognitoServiceFacade;
  }

  @Autowired
  public void setAuthorizeService(
      AuthorizationService authorizeService) {
    this.authorizeService = authorizeService;
  }
}
