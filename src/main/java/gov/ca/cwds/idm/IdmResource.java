package gov.ca.cwds.idm;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.DictionaryProvider;
import gov.ca.cwds.idm.service.IdmService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@Profile("idm")
@RequestMapping(value = "/idm")
public class IdmResource {

  @Autowired private IdmService idmService;

  @Autowired private DictionaryProvider dictionaryProvider;

  @RequestMapping(method = RequestMethod.GET, value = "/users", produces = "application/json")
  @ApiOperation(value = "Users to manage by current logged-in admin", response = User.class)
  public List<User> getUsers(
      @ApiParam(name = "lastName", value = "lastName to search for")
          @RequestParam(name = "lastName", required = false)
          String lastName) {
    return idmService.getUsers(lastName);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/users/{id}", produces = "application/json")
  @ApiOperation(value = "Find User by ID", response = User.class)
  public ResponseEntity<User> getUser(
      @ApiParam(required = true, value = "The unique user ID", example = "userId1")
          @PathVariable
          @NotNull
          String id) {
    return Optional.ofNullable(idmService.findUser(id))
        .map(user -> ResponseEntity.ok().body(user))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @RequestMapping(method = RequestMethod.GET, value = "/permissions", produces = "application/json")
  @ApiOperation(value = "Get List of possible permissions", response = List.class)
  public ResponseEntity<List<String>> getPermissions() {
    return Optional.ofNullable(dictionaryProvider.getPermissions())
        .map(permissions -> ResponseEntity.ok().body(permissions))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
