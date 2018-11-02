package gov.ca.cwds.idm.util;

import static gov.ca.cwds.util.UniversalUserTokenDeserializer.ADMIN_OFFICE_IDS_PARAM;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.COUNTY_NAME_PARAM;
import static gov.ca.cwds.util.Utils.toSet;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.config.api.idm.Roles;
import java.util.Set;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    UniversalUserToken userToken = new UniversalUserToken();
    userToken.setParameter(COUNTY_NAME_PARAM, annotation.county());

    Set<String> roles = toSet(annotation.roles());
    userToken.setRoles(roles);
    if (roles.contains(Roles.OFFICE_ADMIN)) {
      userToken.setParameter(ADMIN_OFFICE_IDS_PARAM, toSet(annotation.adminOfficeIds()));
    }

    userToken.setUserId("userId");

    TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(
        userToken, null, annotation.roles());
    testingAuthenticationToken.setAuthenticated(true);

    context.setAuthentication(testingAuthenticationToken);
    return context;
  }
}
