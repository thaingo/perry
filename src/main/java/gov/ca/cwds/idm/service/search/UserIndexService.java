package gov.ca.cwds.idm.service.search;

import static gov.ca.cwds.idm.persistence.ns.OperationType.CREATE;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.util.Utils.toLowerCase;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class UserIndexService extends BaseSearchIndexService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserIndexService.class);

  public ResponseEntity<String> createUserInIndex(User user) {
    return sendUserToIndex(user, CREATE);
  }

  public ResponseEntity<String> updateUserInIndex(User user) {
    return sendUserToIndex(user, UPDATE);
  }

  private ResponseEntity<String> sendUserToIndex(User user, OperationType operation) {

    final String userId = user.getId();

    ResponseEntity<String> response =
        sendToIndex(user, userId, operation, searchProperties.getUsersIndex());

    if (LOGGER.isInfoEnabled()){
      LOGGER.info(
          "User, username:{} was successfully {}d in Elastic Search index, Dora response string is:{}",
          userId,
          toLowerCase(operation.toString()),
          response.getBody());
    }
    return response;
  }
}
