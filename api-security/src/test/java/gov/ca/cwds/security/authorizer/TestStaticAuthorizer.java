package gov.ca.cwds.security.authorizer;

import gov.ca.cwds.security.realm.PerryAccount;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;

public class TestStaticAuthorizer implements StaticAuthorizer {
  @Override
  public void authorize(PerryAccount perryAccount, SimpleAuthorizationInfo authorizationInfo) {
    authorizationInfo.addObjectPermission(new WildcardPermission("case:read"));
  }
}
