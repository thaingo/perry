package gov.ca.cwds.security.authorizer;

import gov.ca.cwds.security.permission.AbacPermission;
import gov.ca.cwds.security.realm.PerryAccount;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

public class DefaultStaticAuthorizer implements StaticAuthorizer {
  @Override
  public void authorize(PerryAccount perryAccount, SimpleAuthorizationInfo authorizationInfo) {
    authorizationInfo.addRoles(perryAccount.getRoles());
    authorizationInfo.addObjectPermission(new AbacPermission());
  }
}
