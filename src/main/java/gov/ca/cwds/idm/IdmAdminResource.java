package gov.ca.cwds.idm;

import gov.ca.cwds.idm.service.DictionaryProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Profile("idm")
@RequestMapping(value = "/admin")
public class IdmAdminResource {

  @Autowired private DictionaryProvider dictionaryProvider;

  @RequestMapping(method = RequestMethod.PUT, value = "/permissions", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(
    value = {
      @ApiResponse(code = 204, message = "No Content"),
      @ApiResponse(code = 401, message = "Not Authorized")
    }
  )
  @ApiOperation(value = "Substitute the List of possible permissions")
  public ResponseEntity substitutePermissions(
      @ApiParam(required = true, name = "new List of Permissions", value = "The User update data")
          @NotNull
          @RequestBody
          List<String> permissions) {
    dictionaryProvider.rewritePermissions(permissions);
    return ResponseEntity.noContent().build();
  }
}
