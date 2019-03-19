package gov.ca.cwds.smoketest;

import gov.ca.cwds.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component
public class JwtCheck implements HealthIndicator {
  private static final String TEST_IDENTITY = "identity";


  private JwtService jwtService;

  @Override
  public Health health() {
    try {
      String jwt = jwtService.generate("id", "subject", TEST_IDENTITY);
      String jwtContent = jwtService.validate(jwt);
      if (!TEST_IDENTITY.equals(jwtContent)) {
        return Health.down().withDetail("message", "Token validation failed").build();
      }
      return Health.up().build();
    } catch (Exception e) {
      return Health.down().withException(e).build();
    }
  }

  @Autowired
  public void setJwtService(JwtService jwtService) {
    this.jwtService = jwtService;
  }
}