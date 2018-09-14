package gov.ca.cwds.service;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.service.scripts.ScriptTestBase;
import java.util.Set;
import org.junit.Test;

public class UniversalUserTokenDeserializerTest {
  @Test
  public void fromJsonTest() throws Exception {
    String json = ScriptTestBase.readResource("/scripts/default/default-cals.json");
    UniversalUserToken universalUserToken = UniversalUserToken.fromJson(json);
    assert universalUserToken.getUserId().equals("uuid");
    assert universalUserToken.getRoles().size() == 1;
    assert universalUserToken.getRoles().iterator().next().equals(CALS_EXTERNAL_WORKER);
    assert universalUserToken.getParameter("county_name").equals("State of California");

  }

  @Test
  public void fromJsonEmptyRoles() throws Exception {
    String json = ScriptTestBase.readResource("/scripts/default/empty_roles.json");
    UniversalUserToken universalUserToken = UniversalUserToken.fromJson(json);
    assert universalUserToken.getUserId().equals("uuid");
    assert universalUserToken.getRoles().size() == 0;
    assert universalUserToken.getParameter("county_name") == null;
  }

  @Test
  public void testOfficeIdsForOfficeAdmin() throws Exception {
    String json = ScriptTestBase.readResource("/scripts/default/default-office-admin.json");
    UniversalUserToken universalUserToken = UniversalUserToken.fromJson(json);
    assert universalUserToken.getUserId().equals("userabc");
    assert universalUserToken.getRoles().size() == 4;
    Object adminOfficeObj = universalUserToken.getParameter("admin_office_ids");
    assert adminOfficeObj != null;
    assert ((Set<String>) adminOfficeObj).size() == 1;
    assert ((Set<String>) adminOfficeObj).iterator().next().equals("15");
  }

}
