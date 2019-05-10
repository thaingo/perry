package gov.ca.cwds.idm.service.authorization;

import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.List;

public interface AdminActionsAuthorizer {

  void checkCanViewUser();

  void checkCanCreateUser();

  void checkCanUpdateUser();

  void checkCanResendInvitationMessage();

  List<String> getMaxPossibleUserRolesAtCreate();

  List<String> getMaxPossibleUserRolesAtUpdate();

//  void checkCanEditRoles();
}
