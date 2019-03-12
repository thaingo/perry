package gov.ca.cwds.service.scripts;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.service.dto.CwsUserInfo;

import javax.script.ScriptException;
import java.io.IOException;

public class IdmMappingScript extends Script {
  public IdmMappingScript(String path) throws IOException {
    super(path, "result", "cognitoUser", "cwsUser", "nsUser");
  }

  public User map(UserType cognitoUser, CwsUserInfo cwsUser, NsUser nsUser) throws ScriptException {
    User user = new User();
    eval(user, cognitoUser, cwsUser, nsUser);
    return user;
  }
}
