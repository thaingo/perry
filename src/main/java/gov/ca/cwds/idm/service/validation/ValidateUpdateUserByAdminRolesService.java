package gov.ca.cwds.idm.service.validation;

import gov.ca.cwds.idm.dto.User;

@SuppressWarnings({"squid:S1609"})//interface is not used as functional
public interface ValidateUpdateUserByAdminRolesService {
  void validateUpdateUser(User newUser);
}
