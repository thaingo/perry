package gov.ca.cwds.idm;

import gov.ca.cwds.UniversalUserToken;
import java.util.Arrays;
import java.util.HashSet;
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
    userToken.setParameter("county_name", annotation.county());
    userToken.setRoles(new HashSet<>(Arrays.asList(annotation.roles())));
    userToken.setUserId("userId");

    TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(
        userToken, null, annotation.roles());
    testingAuthenticationToken.setAuthenticated(true);

    context.setAuthentication(testingAuthenticationToken);
    return context;
  }
}
