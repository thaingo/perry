package gov.ca.cwds.service.scripts;

import java.util.Arrays;
import gov.ca.cwds.UniversalUserToken;
import org.junit.Test;

/**
 * Created by leonid.marushevskyi on 1/3/2018.
 */
public class DefaultMappingTest extends BaseScriptTest {
  protected UniversalUserToken createUniversalUserToken() {
    UniversalUserToken result = new UniversalUserToken();
    result.setUserId("userId");
    result.getRoles().addAll(Arrays.asList("role1", "role2"));
    result.getPermissions().addAll(Arrays.asList("permission1", "permission2"));
    return result;
  }

  @Test
  public void testGroovyMapping() throws Exception {

    test("/scripts/default/default.groovy", "/scripts/default/default.json", "scripts/default/authz.json");
  }
}
