package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.TokenServiceConfiguration.TOKEN_TRANSACTION_MANAGER;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_CREATE_NEW_USER;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_DELETE_IDM_USER_AT_USER_CREATION_FAIL;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.mapper.NsUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("idm")
public class TransactionalUserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalUserService.class);

  private CognitoServiceFacade cognitoServiceFacade;

  private NsUserService nsUserService;

  private ExceptionFactory exceptionFactory;

  @Transactional(value = TOKEN_TRANSACTION_MANAGER)
  @SuppressWarnings({"fb-contrib:LEST_LOST_EXCEPTION_STACK_TRACE"})//exception with custom constructor is used
  public void createUserInDbWithInvitationEmail(User user) {
    String email = user.getEmail();
    String userId = user.getId();

    try {
      NsUserMapper nsUserMapper = new NsUserMapper();
      NsUser nsUser = nsUserMapper.toNsUser(user);

      nsUserService.create(nsUser);
      cognitoServiceFacade.sendInvitationMessageByEmail(email);

    } catch (Exception userCreateException) {
      try {
        cognitoServiceFacade.deleteCognitoUserById(userId);
      } catch (Exception cognitoUserDeleteException) {
        LOGGER.error("error at attempt to delete new user in Cognito with username: " + userId,
            cognitoUserDeleteException);
        throw exceptionFactory.createPartialSuccessException(
            userId, OperationType.CREATE, UNABLE_TO_DELETE_IDM_USER_AT_USER_CREATION_FAIL,
            userCreateException, cognitoUserDeleteException);
      }
      throw exceptionFactory.createIdmException(
          UNABLE_CREATE_NEW_USER, userCreateException, email);
    }
  }

  /**
   * @return true if User attributes (in Cognito and database) were really updated, false otherwise
   */
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
