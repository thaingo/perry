package gov.ca.cwds.idm.service.validation;

import gov.ca.cwds.idm.dto.User;

public interface ValidateUpdateUserByAdminRolesService {
  void validateUpdateUser(User newUser);
}
