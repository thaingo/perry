package gov.ca.cwds.config;

import org.junit.Test;
import org.springframework.util.Assert;
import springfox.documentation.spring.web.plugins.Docket;

public class SwaggerConfigTest {

  @Test
  public void apiTest() {
    SwaggerConfig swaggerConfig = new SwaggerConfig();
    Docket result = swaggerConfig.api();
    Assert.notNull(result, "Docket is null");
    Assert.notNull(swaggerConfig.api(), "Docket API is null");
  }
}