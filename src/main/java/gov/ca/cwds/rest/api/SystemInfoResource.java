package gov.ca.cwds.rest.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import java.io.IOException;
import java.util.Map;

@RestController
public class SystemInfoResource {

  @Autowired private HealthEndpoint healthEndpoint;

  @Autowired private InfoEndpoint infoEndpoint;

  private static final Logger LOGGER = LoggerFactory.getLogger(SystemInfoResource.class);

  @GET
  @RequestMapping(
    value = "/system-information",
    produces = "application/json",
    method = RequestMethod.GET
  )
  @ApiOperation(value = "Get system info")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "application info and healthcheck")})
  public String getInfo() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      SystemInformation info = new SystemInformation();
      info.setSystemInfo(infoEndpoint.invoke());
      info.setHealth(healthEndpoint.invoke());
      return mapper.writeValueAsString(info);
    } catch (IOException e) {
      LOGGER.error("ERROR in system-info: {}", e.getMessage());
    }
    return null;
  }

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private static class SystemInformation {
    private Map<String, Object> systemInfo;
    private Health health;

    public Health getHealth() {
      return health;
    }

    public void setHealth(Health health) {
      this.health = health;
    }

    public Map<String, Object> getSystemInfo() {
      return systemInfo;
    }

    public void setSystemInfo(Map<String, Object> info) {
      this.systemInfo = info;
    }
  }
}
