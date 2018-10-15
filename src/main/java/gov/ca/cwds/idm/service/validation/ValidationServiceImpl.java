package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByRacfId;
import static gov.ca.cwds.service.messages.MessageCode.ACTIVE_USER_WITH_RAFCID_EXISTS_IN_IDM;
import static gov.ca.cwds.service.messages.MessageCode.NO_USER_WITH_RACFID_IN_CWSCMS;
import static gov.ca.cwds.util.Utils.toUpperCase;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import gov.ca.cwds.rest.api.domain.UserIdmValidationException;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import gov.ca.cwds.service.messages.MessagesService;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@Profile("idm")
public class ValidationServiceImpl implements ValidationService {

  private UserByAdminRolesValidator userByAdminRolesValidator;

  private MappingService mappingService;

  private CwsUserInfoService cwsUserInfoService;

  private MessagesService messages;

  private CognitoServiceFacade cognitoServiceFacade;

  @Override
  public void validateUserCreate(UniversalUserToken admin, User user) {

  }

  @Override
  public void validateUpdateUser(UniversalUserToken admin, UserType existedCognitoUser, UserUpdate updateUserDto) {
    User newUser = getNewUser(existedCognitoUser, updateUserDto);
    userByAdminRolesValidator.validate(newUser);

    validateActivateUser(existedCognitoUser, updateUserDto);
  }

  @Override
  public boolean isActiveRacfIdPresent(String racfId) {
    Collection<UserType> cognitoUsersByRacfId =
        cognitoServiceFacade.searchAllPages(composeToGetFirstPageByRacfId(toUpperCase(racfId)));
    return !CollectionUtils.isEmpty(cognitoUsersByRacfId)
        && isActiveUserPresent(cognitoUsersByRacfId);
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
    if (StringUtils.isEmpty(racfId)) {
      return;
    }
    validateActivateUser(racfId);
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
}
