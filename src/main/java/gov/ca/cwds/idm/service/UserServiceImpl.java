package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_DELETE_USER_AT_USER_CREATION_FAIL;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_SEND_INVITATION_EMAIL_AT_USER_CREATION;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("idm")
public class UserServiceImpl implements UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  private CognitoServiceFacade cognitoServiceFacade;

  private NsUserService nsUserService;

  private ExceptionFactory exceptionFactory;

  @Override
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public void createUserInDbWithInvitationEmail(User user) {
    String email = user.getEmail();
    String userId = user.getId();

    try {
      cognitoServiceFacade.sendInvitationMessageByEmail(email);
    } catch (Exception invitationEmailException) {
      LOGGER.error(
          "error at sending invitation email for new user with id:" + userId + ", email:" + email,
          invitationEmailException);
      try {
        cognitoServiceFacade.deleteCognitoUserById(userId);
      } catch (Exception cognitoUserDeleteException) {
        LOGGER.error("error at attempt to delete new user with id:" + userId,
            cognitoUserDeleteException);
        throw exceptionFactory.createPartialSuccessException(
            userId, OperationType.CREATE, UNABLE_TO_DELETE_USER_AT_USER_CREATION_FAIL,
            invitationEmailException, cognitoUserDeleteException);
      }
      throw exceptionFactory.createIdmException(
          UNABLE_TO_SEND_INVITATION_EMAIL_AT_USER_CREATION, invitationEmailException, email);
    }
  }

  /**
   * @return true if User attributes (in Cognito and database) were really updated, false otherwise
   */
  @Override
  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  public boolean updateUserAttributes(UserUpdateRequest userUpdateRequest) {
    boolean isDatabaseUpdated = nsUserService.update(userUpdateRequest);
    boolean isCognitoUpdated = cognitoServiceFacade.updateUserAttributes(userUpdateRequest);
    return (isDatabaseUpdated || isCognitoUpdated);
  }

  @Autowired
  public void setCognitoServiceFacade(CognitoServiceFacade cognitoServiceFacade) {
    this.cognitoServiceFacade = cognitoServiceFacade;
  }

  @Autowired
  public void setNsUserService(NsUserService nsUserService) {
    this.nsUserService = nsUserService;
  }

  @Autowired
  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
