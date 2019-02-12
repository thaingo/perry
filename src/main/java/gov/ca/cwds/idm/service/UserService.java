package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;

public interface UserService {

  void createUserInDbWithInvitationEmail(User user);

  boolean updateUserAttributes(UserUpdateRequest userUpdateRequest);
}
