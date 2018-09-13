package gov.ca.cwds.service.scripts;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;

import gov.ca.cwds.UniversalUserToken;
import org.junit.Test;

import java.util.Collections;

public class DefaultMappingCalsTest extends ScriptTestBase {
  @Override
  protected UniversalUserToken createUniversalUserToken() {
    UniversalUserToken universalUserToken = super.createUniversalUserToken();
    universalUserToken.setUserId("uuid");
    universalUserToken.setRoles(Collections.singleton(CALS_EXTERNAL_WORKER));
    universalUserToken.setParameter("custom:county", "State of California");
    universalUserToken.setParameter("given_name", "first");
    universalUserToken.setParameter("family_name", "last");
    universalUserToken.setParameter("email", "e-mail");
    universalUserToken.setParameter("userName", "testUserName");
    universalUserToken.setParameter("custom:office", "15");
    return universalUserToken;
  }

  @Test
  public void testGroovyMapping() throws Exception {
    test("/scripts/default/default.groovy",
        "/scripts/default/default-cals.json",
        null);
  }
}
