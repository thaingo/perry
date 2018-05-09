package gov.ca.cwds.config;

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.ArrayList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfig {

  private static final Contact DEFAULT_CONTACT = new Contact(
      "", "", "");

  private static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
      "Perry API", "RESTful Perry API", "",
      "", DEFAULT_CONTACT,
      "", "", new ArrayList<>());

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(DEFAULT_API_INFO)
        .select()
        .paths(not(or(
            regex("/error"),// Exclude Spring error controller
            regex("/") //Exclude Home Page Url controller
        )))
        .build();
  }
}
