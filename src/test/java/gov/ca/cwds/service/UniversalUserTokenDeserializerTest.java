package gov.ca.cwds.service;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.service.scripts.BaseScriptTest;
import org.junit.Test;

public class UniversalUserTokenDeserializerTest {
  @Test
  public void fromJsonTest() throws Exception {
    String json = BaseScriptTest.readResource("/scripts/default/default-cals.json");
    UniversalUserToken universalUserToken = UniversalUserToken.fromJson(json);
    assert universalUserToken.getUserId().equals("uuid");
    assert universalUserToken.getRoles().size() == 1;
    assert universalUserToken.getRoles().iterator().next().equals("External CALS");
    assert universalUserToken.getParameter("county_name").equals("State of California");

  }

  @Test
  public void fromJsonEmptyRoles() throws Exception {
    String json = BaseScriptTest.readResource("/scripts/default/empty_roles.json");
    UniversalUserToken universalUserToken = UniversalUserToken.fromJson(json);
    assert universalUserToken.getUserId().equals("uuid");
    assert universalUserToken.getRoles().size() == 0;
    assert universalUserToken.getParameter("county_name") == null;
  }

}
