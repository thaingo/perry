package gov.ca.cwds.service.scripts;

import org.junit.Test;

/**
 * Created by dmitry.rudenko on 7/25/2017.
 */
public class IdentityMappingScriptTest extends ScriptTestBase {
  @Test
  public void testGroovyMapping() throws Exception {

    test("/scripts/basic/test.groovy", "/scripts/basic/test.json", "scripts/basic/authz.json");
  }
}
