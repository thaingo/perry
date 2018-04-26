package gov.ca.cwds.idm;

import gov.ca.cwds.idm.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/idm")
public class IdmResource {

  @Autowired private IdmService idmService;

  @RequestMapping(method = RequestMethod.GET, value = "/users", produces = "application/json")
  public List<User> getUsers() {
    return idmService.getUsers();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/roles", produces = "application/json")
  public List<User> getRoles() {
    return idmService.getRoles();
  }
}
