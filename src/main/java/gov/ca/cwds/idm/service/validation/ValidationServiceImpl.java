package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByEmail;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByRacfId;
import static gov.ca.cwds.idm.service.PossibleUserPermissionsService.CANS_PERMISSION_NAME;
import static gov.ca.cwds.service.messages.MessageCode.ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM;
import static gov.ca.cwds.service.messages.MessageCode.COUNTY_NAME_IS_NOT_PROVIDED;
import static gov.ca.cwds.service.messages.MessageCode.FIRST_NAME_IS_NOT_PROVIDED;
import static gov.ca.cwds.service.messages.MessageCode.LAST_NAME_IS_NOT_PROVIDED;
import static gov.ca.cwds.service.messages.MessageCode.NO_USER_WITH_RACFID_IN_CWSCMS;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_CREATE_NON_RACFID_USER_WITH_CANS_PERMISSION;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_CREATE_USER_WITHOUT_ROLES;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_REMOVE_ALL_ROLES;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_ASSIGN_CANS_PERMISSION_TO_NON_RACFID_USER;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_UPDATE_UNALLOWED_ROLES;
import static gov.ca.cwds.service.messages.MessageCode.USER_WITH_EMAIL_EXISTS_IN_IDM;
import static gov.ca.cwds.util.Utils.isRacfidUser;
import static gov.ca.cwds.util.Utils.toCommaDelimitedString;
import static gov.ca.cwds.util.Utils.toUpperCase;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import gov.ca.cwds.service.messages.MessageCode;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class ValidationServiceImpl implements ValidationService {

  private CwsUserInfoService cwsUserInfoService;

  private CognitoServiceFacade cognitoServiceFacade;

  private AdminRoleImplementorFactory adminRoleImplementorFactory;

  private ExceptionFactory exceptionFactory;

  @Override
  public void validateUserCreate(User enrichedUser, boolean activeUserExistsInCws) {

    validateFirstNameIsProvided(enrichedUser);
    validateLastNameIsProvided(enrichedUser);
    validateCountyNameIsProvided(enrichedUser);

    validateUserRolesExistAtCreate(enrichedUser);
    validateUserRolesAreAllowedAtCreate(enrichedUser);

    validateCreateByCansPermission(enrichedUser);

    validateActiveRacfidUserExistsInCws(activeUserExistsInCws, enrichedUser.getRacfid());
    validateRacfidDoesNotExistInCognito(enrichedUser.getRacfid());
  }

  @Override
  public void validateVerifyIfUserCanBeCreated(User enrichedUser, boolean activeUserExistsInCws) {
    validateActiveRacfidUserExistsInCws(activeUserExistsInCws, enrichedUser.getRacfid());
    validateEmailDoesNotExistInCognito(enrichedUser.getEmail());
    validateRacfidDoesNotExistInCognito(enrichedUser.getRacfid());
  }

  @Override
  public void validateUpdateUser(UserType existedCognitoUser, UserUpdate updateUserDto) {
    validateNotAllRolesAreRemovedAtUpdate(updateUserDto);
    validateNewUserRolesAreAllowedAtUpdate(updateUserDto);
    validateUpdateByCansPermission(existedCognitoUser, updateUserDto);
    validateActivateUser(existedCognitoUser, updateUserDto);
  }

  private void validateFirstNameIsProvided(User user) {
    validateIsNotBlank(user.getFirstName(), FIRST_NAME_IS_NOT_PROVIDED);
  }

  private void validateLastNameIsProvided(User user) {
    validateIsNotBlank(user.getLastName(), LAST_NAME_IS_NOT_PROVIDED);
  }

  private void validateCountyNameIsProvided(User user) {
    validateIsNotBlank(user.getCountyName(), COUNTY_NAME_IS_NOT_PROVIDED);
  }

  private void validateIsNotBlank(String value, MessageCode errorCode) {
    if (StringUtils.isBlank(value)) {
      throwValidationException(errorCode);
    }
  }

  private void validateActiveRacfidUserExistsInCws(boolean activeUserExistsInCws, String racfId) {
    if(!isRacfidUser(racfId)){
      return;
    }

    if (!activeUserExistsInCws) {
      throwValidationException(NO_USER_WITH_RACFID_IN_CWSCMS, racfId);
    }
  }

  private void validateUserRolesExistAtCreate(User user) {
    Collection<String> roles = user.getRoles();

    if(roles == null || roles.isEmpty()) {
      throwValidationException(UNABLE_TO_CREATE_USER_WITHOUT_ROLES);
    }
  }

  private void validateUserRolesAreAllowedAtCreate(User user) {
    validateByAllowedRoles(user.getRoles(), UNABLE_TO_CREATE_USER_WITH_UNALLOWED_ROLES);
  }

  void validateRacfidDoesNotExistInCognito(String racfId) {
    if(!isRacfidUser(racfId)){
      return;
    }

    if (isActiveRacfIdPresentInCognito(racfId)) {
      throwValidationException(ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM, racfId);
    }
  }

  private void validateEmailDoesNotExistInCognito(String email) {
    if (userWithEmailExistsInCognito(email)) {
      throwValidationException(USER_WITH_EMAIL_EXISTS_IN_IDM, email);
    }
  }

  private void validateNotAllRolesAreRemovedAtUpdate(UserUpdate updateUserDto) {
    Collection<String> newUserRoles = updateUserDto.getRoles();

    if (newUserRoles == null) {//it means that roles are not edited
      return;
    }

    if (newUserRoles.isEmpty()) {
      throwValidationException(UNABLE_TO_REMOVE_ALL_ROLES);
    }
  }

  private void validateNewUserRolesAreAllowedAtUpdate(UserUpdate updateUserDto) {
    validateByAllowedRoles(updateUserDto.getRoles(), UNABLE_UPDATE_UNALLOWED_ROLES);
  }

  private void validateByAllowedRoles(Collection<String> roles, MessageCode errorCode) {

    if (roles == null) {
      return;
    }

    Collection<String> allowedRoles = adminRoleImplementorFactory.getPossibleUserRoles();
    if (!allowedRoles.containsAll(roles)) {
      throwValidationException(
          errorCode,
          toCommaDelimitedString(roles),
          toCommaDelimitedString(allowedRoles));
    }
  }

  private void validateCreateByCansPermission(User user) {
    validateByCansPermission(user.getPermissions(), isRacfidUser(user), user.getId(),
        UNABLE_TO_CREATE_NON_RACFID_USER_WITH_CANS_PERMISSION);
  }

  private void validateUpdateByCansPermission(UserType existedCognitoUser, UserUpdate updateUserDto) {
    validateByCansPermission(updateUserDto.getPermissions(), isRacfidUser(existedCognitoUser),
        existedCognitoUser.getUsername(), UNABLE_TO_ASSIGN_CANS_PERMISSION_TO_NON_RACFID_USER);
  }

  private void validateByCansPermission(Collection<String> newUserPermissions, boolean isRacfidUser,
      String userId, MessageCode errorCode) {
    if (newUserPermissions == null) {
      return;
    }

    if (!isRacfidUser && newUserPermissions.contains(CANS_PERMISSION_NAME)) {
      throwValidationException(errorCode, userId);
    }
  }

  private boolean isActiveRacfIdPresentInCognito(String racfId) {
    Collection<UserType> cognitoUsersByRacfId =
        cognitoServiceFacade.searchAllPages(composeToGetFirstPageByRacfId(toUpperCase(racfId)));
    return !CollectionUtils.isEmpty(cognitoUsersByRacfId)
        && isActiveUserPresent(cognitoUsersByRacfId);
  }

  private boolean userWithEmailExistsInCognito(String email) {
    Collection<UserType> cognitoUsers =
        cognitoServiceFacade.searchPage(composeToGetFirstPageByEmail(email)).getUsers();
    return !CollectionUtils.isEmpty(cognitoUsers);
  }

  private void throwValidationException(MessageCode messageCode, String... args) {
    throw exceptionFactory.createValidationException(messageCode, args);
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

  void validateActivateUser(String racfId ) {
    CwsUserInfo cwsUser = cwsUserInfoService.getCwsUserByRacfId(racfId);
    validateActiveRacfidUserExistsInCws(cwsUser != null, racfId);
    validateRacfidDoesNotExistInCognito(racfId);
  }

  private static boolean isActiveUserPresent(Collection<UserType> cognitoUsers) {
    return cognitoUsers
        .stream()
        .anyMatch(userType -> Objects.equals(userType.getEnabled(), Boolean.TRUE));
  }

  @Autowired
  public void setCwsUserInfoService(CwsUserInfoService cwsUserInfoService) {
    this.cwsUserInfoService = cwsUserInfoService;
  }

  @Autowired
  public void setCognitoServiceFacade(
      CognitoServiceFacade cognitoServiceFacade) {
    this.cognitoServiceFacade = cognitoServiceFacade;
  }

  @Autowired
  public void setAdminRoleImplementorFactory(
      AdminRoleImplementorFactory adminRoleImplementorFactory) {
    this.adminRoleImplementorFactory = adminRoleImplementorFactory;
  }

  @Autowired
  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
