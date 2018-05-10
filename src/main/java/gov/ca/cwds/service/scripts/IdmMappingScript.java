package gov.ca.cwds.service.scripts;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.service.dto.CwsUserInfo;

import javax.script.ScriptException;
import java.io.IOException;

public class IdmMappingScript extends Script {
  public IdmMappingScript(String path) throws IOException {
    super(path, "result", "cognitoUser", "cwsUser");
  }

  public User map(AdminGetUserResult cognitoUser, CwsUserInfo userInfo) throws ScriptException {
    User user = new User();
    eval(user, cognitoUser, userInfo);
    return user;
  }
}
