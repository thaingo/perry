package gov.ca.cwds.service.scripts;

import static gov.ca.cwds.config.api.idm.Role.CWS_ADMIN;

import java.util.Arrays;
import java.util.List;

import gov.ca.cwds.UniversalUserToken;
import org.junit.Test;

/**
 * Created by leonid.marushevskyi on 1/3/2018.
 */
public class DefaultMappingTest extends BaseScriptTest {

  private List<String> roles;

  protected UniversalUserToken createUniversalUserToken() {
    UniversalUserToken result = new UniversalUserToken();
    result.setUserId("userId");
    result.getRoles().addAll(roles);
    result.setParameter("userName", "testUserName");
    result.getPermissions().addAll(Arrays.asList("permission1", "permission2"));
    return result;
  }

  @Test
  public void testGroovyMapping() throws Exception {
    roles = Arrays.asList("role1", "role2");
    test("/scripts/default/default.groovy", "/scripts/default/default.json", "scripts/default/authz.json");
  }

  @Test
  public void testGroovyMappingNonRacf() throws Exception {
    roles = Arrays.asList("role1", "role2");
    test("/scripts/default/default.groovy", "/scripts/default/default-nonracf.json", null);
  }

  @Test
  public void testGroovyMappingCWSAdmin() throws Exception {
    roles = Arrays.asList("role1", "role2", CWS_ADMIN);
    test("/scripts/default/default.groovy", "/scripts/default/default-admin.json", "scripts/default/authz.json");
  }

  @Test
  public void testGroovyMappingNonRacfIdCWSAdmin() throws Exception {
    roles = Arrays.asList("role1", "role2", CWS_ADMIN);
    test("/scripts/default/default.groovy", "/scripts/default/default-nonracf-admin.json", null);
  }
}
