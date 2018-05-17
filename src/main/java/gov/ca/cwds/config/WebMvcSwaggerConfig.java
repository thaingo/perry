package gov.ca.cwds.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;


@Configuration
@Profile("swagger")
public class WebMvcSwaggerConfig extends WebMvcConfig {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    super.addViewControllers(registry);
    registry.addRedirectViewController("/documentation/v2/api-docs", "/v2/api-docs");
    registry.addRedirectViewController("/documentation/configuration/ui", "/configuration/ui");
    registry.addRedirectViewController("/documentation/configuration/security", "/configuration/security");
    registry.addRedirectViewController("/documentation/swagger-resources", "/swagger-resources");
    registry.addRedirectViewController("/documentation", "/documentation/swagger-ui.html");
    registry.addRedirectViewController("/documentation/", "/documentation/swagger-ui.html");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    super.addResourceHandlers(registry);
    registry
        .addResourceHandler("/documentation/**")
        .addResourceLocations("classpath:/swagger-static/");
    registry
        .addResourceHandler("swagger-ui.html", "authorize.html")
        .addResourceLocations("classpath:/swagger-static/");
  }
}
