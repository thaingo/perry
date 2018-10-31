package gov.ca.cwds.idm.util;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
  String COUNTY = "Yolo";
  String OFFICE_ID = "JzoGSkE05I";

  String[] roles() default COUNTY_ADMIN;

  String county() default COUNTY;

  String[] adminOfficeIds() default OFFICE_ID;
}
