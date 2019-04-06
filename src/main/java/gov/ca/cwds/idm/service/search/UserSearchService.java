package gov.ca.cwds.idm.service.search;

import static gov.ca.cwds.service.messages.MessageCode.UNABLE_CREATE_IDM_USER_IN_ES;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_UPDATE_IDM_USER_IN_ES;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import gov.ca.cwds.idm.service.UserLogService;
import gov.ca.cwds.idm.service.execution.OptionalExecution;
import gov.ca.cwds.idm.service.execution.PutInSearchExecution;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class UserSearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserSearchService.class);

  @Autowired
  private UserIndexService userIndexService;

  @Autowired
  private UserLogService userLogService;

  @Autowired
  private MessagesService messages;

  public PutInSearchExecution<User> createUserInSearch(User user) {
    return putUserInSearch(
        user,
        userIndexService::createUserInIndex,
        userLogService::logCreate,
        UNABLE_CREATE_IDM_USER_IN_ES);
  }

  public PutInSearchExecution<User> updateUserInSearch(User updatedUser) {
    return putUserInSearch(
        updatedUser,
        userIndexService::updateUserInIndex,
        userLogService::logUpdate,
        UNABLE_UPDATE_IDM_USER_IN_ES);
  }

  private PutInSearchExecution<User> putUserInSearch(
      User user,
      Function<User, ResponseEntity<String>> tryOperation,
      Function<String, OptionalExecution<String, UserLog>> catchOperation,
      MessageCode errorCode) {

    return new PutInSearchExecution<User>(user) {
      @Override
      protected ResponseEntity<String> tryMethod(User user) {
        return tryOperation.apply(user);
      }

      @Override
      protected void catchMethod(Exception e) {
        String msg = messages.getTechMessage(errorCode, user.getId());
        LOGGER.error(msg, e);
        setUserLogExecution(catchOperation.apply(user.getId()));
      }
    };
  }

  public void setUserIndexService(UserIndexService userIndexService) {
    this.userIndexService = userIndexService;
  }
}
