package gov.ca.cwds.service.scripts;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;

import javax.script.ScriptException;
import java.io.IOException;

public class IdmMappingScript extends Script {
  public IdmMappingScript(String path) throws IOException {
    super(path, "result", "cognitoUser", "cwsUser");
  }

  public User map(AdminGetUserResult cognitoUser, UserAuthorization userInfo) throws ScriptException {
    User user = new User();
    eval(user, cognitoUser, userInfo);
    return user;
  }
}
