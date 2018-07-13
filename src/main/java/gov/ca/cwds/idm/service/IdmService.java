package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.dto.UserVerificationResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IdmService {
  @PostFilter("filterObject.countyName == principal.getParameter('county_name')")
  List<User> getUsers(String lastName);

  @PostAuthorize("returnObject.countyName == principal.getParameter('county_name')")
  User findUser(String id);

  void updateUser(String id, UserUpdate updateUserDto);

  UserVerificationResult verifyUser(String racfId, String email);

  @PreAuthorize("#user.countyName == principal.getParameter('county_name')")
  String createUser(@P("user") User user);
}
