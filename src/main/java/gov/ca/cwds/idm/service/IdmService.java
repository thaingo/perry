package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.UpdateUserDto;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserVerificationResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IdmService {
  @PostFilter("filterObject.countyName == principal.getParameter('county_name')")
  List<User> getUsers(String lastName);

  @PostAuthorize("returnObject.countyName == principal.getParameter('county_name')")
  User findUser(String id);

  @PreAuthorize("@cognitoServiceFacade.getCountyName(#id) == principal.getParameter('county_name')")
  void updateUser(@P("id") String id, UpdateUserDto updateUserDto);

  UserVerificationResult verifyUser(String racfId, String email);
}
