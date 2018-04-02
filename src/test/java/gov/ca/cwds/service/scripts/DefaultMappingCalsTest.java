package gov.ca.cwds.service.scripts;

import gov.ca.cwds.UniversalUserToken;
import org.junit.Test;

import java.util.Collections;

public class DefaultMappingCalsTest extends BaseScriptTest {
  @Override
  protected UniversalUserToken createUniversalUserToken() {
    UniversalUserToken universalUserToken = super.createUniversalUserToken();
    universalUserToken.setUserId("uuid");
    universalUserToken.setRoles(Collections.singleton("External CALS"));
    return universalUserToken;
  }

  @Test
  public void testGroovyMapping() throws Exception {
    test("/scripts/default/default.groovy",
        "/scripts/default/default-cals.json",
        "scripts/default/authz-cals.json");
  }
}
