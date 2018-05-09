package gov.ca.cwds.service.scripts;

import gov.ca.cwds.UniversalUserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by dmitry.rudenko on 7/28/2017.
 */
public class IdpMappingScript extends Script {
  private final static Logger log = LoggerFactory.getLogger(IdpMappingScript.class);
  public IdpMappingScript(String path) throws IOException {
    super(path, "universalUserToken", "idpToken");
  }

  public UniversalUserToken map(Map idpToken) throws ScriptException {
    log.debug("IDP TOKEN: {}", idpToken.toString());
    UniversalUserToken universalUserToken = new UniversalUserToken();
    eval(universalUserToken, idpToken);
    return universalUserToken;
  }
}
