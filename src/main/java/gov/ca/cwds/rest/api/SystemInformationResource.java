package gov.ca.cwds.rest.api;

import static gov.ca.cwds.config.Constants.HTTP_STATUS_NOT_HEALTHY;
import static gov.ca.cwds.util.Utils.isStatusHealthy;
import static gov.ca.cwds.util.Utils.healthCheckUtcTimeToPacific;

import gov.ca.cwds.dto.app.HealthCheckResultDto;
import gov.ca.cwds.dto.app.SystemInformationDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import java.util.Map;

@RestController
public class SystemInformationResource {

  @Autowired
  private InfoEndpoint infoEndpoint;

  @Autowired
  private HealthEndpoint healthEndpoint;

  @GET
  @RequestMapping(
      value = "/system-information",
      produces = "application/json",
      method = RequestMethod.GET
  )
  @ApiOperation(value = "Get system info")
  @ApiResponses(
      value = {
          @ApiResponse(code = 200, message = "application info and healthcheck"),
          @ApiResponse(code = 465, message = "CARES Service is not healthy")
      }
  )
  public ResponseEntity<SystemInformationDto> getInfo() {
    Object infoObj = infoEndpoint.invoke().get("build");
    Health health = healthEndpoint.invoke();
    SystemInformationDto systemInformation = prepareSystemInformation(infoObj, health);
    int statusCode =
        systemInformation.isHealthStatus() ? HttpStatus.OK.value() : HTTP_STATUS_NOT_HEALTHY;
    return ResponseEntity.status(statusCode).body(systemInformation);
  }

  @SuppressWarnings("unchecked")
  private SystemInformationDto prepareSystemInformation(Object infoObj, Health health) {
    Map<String, String> info = (Map<String, String>) infoObj;

    SystemInformationDto systemInformation = new SystemInformationDto();
    systemInformation.setApplicationName(toRealNull(info.get("name")));
    systemInformation.setVersion(toRealNull(info.get("version")));
    systemInformation.setBuildNumber(toRealNull(info.get("buildNumber")));
    systemInformation.setHealthStatus(isStatusHealthy(health.getStatus()));

    String time = healthCheckUtcTimeToPacific(info.get("time"));
    addHealthCheckResults(systemInformation, health, time);

    return systemInformation;
  }

  private void addHealthCheckResults(SystemInformationDto systemInformation, Health health,
      String timestamp) {
    for (Map.Entry<String, Object> healthEntry : health.getDetails().entrySet()) {
      if ("db".equals(healthEntry.getKey())) {
        addHealthCheckResults(systemInformation, objectToHealth(healthEntry.getValue()), timestamp);
      } else {
        systemInformation.getHealthCheckResults().put(healthEntry.getKey(),
            new HealthCheckResultDto(objectToHealth(healthEntry.getValue()), timestamp));
      }
    }
  }

  // this is needed as a separate method for proper testing, see SystemInformationResourceTest
  Health objectToHealth(Object obj) {
    return (Health) obj;
  }

  private String toRealNull(String s) {
    return "null".equals(s) ? null : s;
  }
}
