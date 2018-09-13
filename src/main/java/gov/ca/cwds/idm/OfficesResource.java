package gov.ca.cwds.idm;


import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.idm.service.OfficeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("idm")
@RequestMapping(value = "/idm")
public class OfficesResource {

  @Autowired
  private OfficeService officeService;

  @RequestMapping(
      method = RequestMethod.GET,
      value = "/offices",
      produces = "application/json"
  )
  @ApiResponses(value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
  })
  @ApiOperation(
      value = "Get list of offices",
      response = CwsOffice.class,
      responseContainer = "List"
  )
  public ResponseEntity getOffices() {
    return ResponseEntity.ok().body(officeService.getOffices());
  }

}
