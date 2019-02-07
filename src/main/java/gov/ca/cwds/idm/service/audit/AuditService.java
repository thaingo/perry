package gov.ca.cwds.idm.service.audit;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import gov.ca.cwds.idm.service.diff.BooleanDiff;

public interface AuditService {

  void auditUserCreate(User user);

  void auditUserRegistrationResent(User user);

  void auditUserEnableStatusUpdate(User existedUser, BooleanDiff enabledDiff);

  void auditUserUpdate(UserUpdateRequest userUpdateRequest);
}
