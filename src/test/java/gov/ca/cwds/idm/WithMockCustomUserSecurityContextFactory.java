package gov.ca.cwds.idm;

import static gov.ca.cwds.util.UniversalUserTokenDeserializer.ADMIN_OFFICE_IDS_PARAM;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.COUNTY_NAME_PARAM;
import static gov.ca.cwds.util.Utils.toSet;

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
    userToken.setParameter(COUNTY_NAME_PARAM, annotation.county());
    userToken.setRoles(new HashSet<>(Arrays.asList(annotation.roles())));
    userToken.setParameter(ADMIN_OFFICE_IDS_PARAM, toSet(annotation.adminOfficeIds()));
    userToken.setUserId("userId");

    TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(
        userToken, null, annotation.roles());
    testingAuthenticationToken.setAuthenticated(true);

    context.setAuthentication(testingAuthenticationToken);
    return context;
  }
}
