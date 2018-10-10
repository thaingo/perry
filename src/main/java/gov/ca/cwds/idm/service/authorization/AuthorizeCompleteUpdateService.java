package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.User;

public interface AuthorizeCompleteUpdateService {
  boolean canCompleteUpdateUser(User newUser);
}
