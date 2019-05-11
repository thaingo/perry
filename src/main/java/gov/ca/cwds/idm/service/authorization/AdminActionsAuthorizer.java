package gov.ca.cwds.idm.service.authorization;

import java.util.List;

public interface AdminActionsAuthorizer {

  void checkCanViewUser();

  void checkCanCreateUser();

  void checkCanUpdateUser();

  void checkCanResendInvitationMessage();

  List<String> getMaxAllowedUserRolesAtCreate();

  List<String> getMaxAllowedUserRolesAtUpdate();
}
