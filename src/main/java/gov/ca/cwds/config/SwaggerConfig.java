package gov.ca.cwds.config;

import io.swagger.models.auth.In;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfig {

  private static final Contact DEFAULT_CONTACT = new Contact("", "", "");

  private static final ApiInfo DEFAULT_API_INFO =
      new ApiInfo(
          "Perry API", "RESTful Perry API", "", "", DEFAULT_CONTACT, "", "", new ArrayList<>());

  public static final String AUTHORIZATION = "Authorization";
  public static final String TOKEN = "token";

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .useDefaultResponseMessages(false)
        .apiInfo(DEFAULT_API_INFO)
        .select()
        .paths(
            not(
                or(
                    regex("/error"), // Exclude Spring error controller
                    regex("/") // Exclude Home Page Url controller
                    )))
        .build()
        .securitySchemes(newArrayList(apiKey()))
        .securityContexts(newArrayList(securityContext()));
  }

  private ApiKey apiKey() {
    return new ApiKey(AUTHORIZATION, TOKEN, In.QUERY.name());
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.ant("/idm/**"))
        .build();
  }

  List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return newArrayList(new SecurityReference(AUTHORIZATION, authorizationScopes));
  }
}
