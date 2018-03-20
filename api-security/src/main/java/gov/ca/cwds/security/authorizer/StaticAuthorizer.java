package gov.ca.cwds.security.authorizer;

import gov.ca.cwds.security.realm.PerryAccount;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

public interface StaticAuthorizer {
  void authorize(PerryAccount perryAccount, SimpleAuthorizationInfo authorizationInfo);
}
