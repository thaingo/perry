package gov.ca.cwds.idm;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/idm")
public class IdmResource {

  @Autowired private IdmService idmService;

  @Autowired private PermissionService permissionService;

  @RequestMapping(method = RequestMethod.GET, value = "/users", produces = "application/json")
  public List<User> getUsers() {
    return idmService.getUsers();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/permissions", produces = "application/json")
  public List<String> getPermissions() {
    return permissionService.getPermissionNames();
  }
}
