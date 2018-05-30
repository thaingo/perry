package gov.ca.cwds.service.scripts;

import gov.ca.cwds.UniversalUserToken;
import org.junit.Test;

import java.util.Collections;

public class DefaultMappingCalsTest extends BaseScriptTest {
  @Override
  protected UniversalUserToken createUniversalUserToken() {
    UniversalUserToken universalUserToken = super.createUniversalUserToken();
    universalUserToken.setUserId("uuid");
    universalUserToken.setRoles(Collections.singleton("CALS-external-worker"));
    universalUserToken.setParameter("custom:county", "State of California");
    universalUserToken.setParameter("given_name", "first");
    universalUserToken.setParameter("family_name", "last");
    universalUserToken.setParameter("email", "e-mail");
    universalUserToken.setParameter("userName", "testUserName");
    return universalUserToken;
  }

  @Test
  public void testGroovyMapping() throws Exception {
    test("/scripts/default/default.groovy",
        "/scripts/default/default-cals.json",
        null);
  }
}
