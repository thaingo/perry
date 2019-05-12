package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.List;

public interface AdminActionsAuthorizer {

  void checkCanViewUser();

  void checkCanCreateUser();

  void checkCanUpdateUser(UserUpdate userUpdate);

  void checkCanResendInvitationMessage();

  List<String> getMaxAllowedUserRolesAtUpdate();
}
