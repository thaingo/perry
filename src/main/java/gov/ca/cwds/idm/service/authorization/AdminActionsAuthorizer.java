package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.UserUpdate;

public interface AdminActionsAuthorizer {

  void checkCanViewUser();

  void checkCanCreateUser();

  void checkCanUpdateUser(UserUpdate userUpdate);

  void checkCanResendInvitationMessage();
}
