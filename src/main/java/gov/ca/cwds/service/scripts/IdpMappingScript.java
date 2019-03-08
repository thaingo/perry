package gov.ca.cwds.service.scripts;

import gov.ca.cwds.UniversalUserToken;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by dmitry.rudenko on 7/28/2017.
 */
public class IdpMappingScript extends Script {
  public IdpMappingScript(String path) throws IOException {
    super(path, "universalUserToken", "idpToken", "nsUser");
  }

  public UniversalUserToken map(Map idpToken, NsUser nsUser) throws ScriptException {
    UniversalUserToken universalUserToken = new UniversalUserToken();
    eval(universalUserToken, idpToken, nsUser);
    return universalUserToken;
  }
}
