package gov.ca.cwds.idm;

import static gov.ca.cwds.config.api.idm.Role.CWS_ADMIN;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
  String COUNTY = "Yolo";
  String county() default COUNTY;
  String[] roles() default CWS_ADMIN;
}
