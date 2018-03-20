package gov.ca.cwds.security.realm;

import gov.ca.cwds.security.AbstractApiSecurityTest;
import gov.ca.cwds.security.PerryShiroToken;
import gov.ca.cwds.security.jwt.JwtConfiguration;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class JwtRealmTest extends AbstractApiSecurityTest {

  private static JwtRealm jwtRealm;

  @BeforeClass
  public static void init() throws Exception {
    jwtRealm = new JwtRealmMock();
    jwtRealm.setKeyStoreAlias("test");
    jwtRealm.setKeyStoreKeyPassword("test");
    jwtRealm.setKeyStorePassword("test");
    jwtRealm.setTokenIssuer("tokenIssuer");
    jwtRealm.setEncKeyAlias("enc128");
    jwtRealm.setEncKeyPassword("test");
    jwtRealm.setUseEncryption(true);
    jwtRealm.setEncryptionMethod("A128GCM");
    jwtRealm.setKeyStorePath(Paths.get(JwtRealmTest.class.getResource("/security/enc.jceks").toURI()).toAbsolutePath().toString());
    jwtRealm.onInit();
  }

  @Test
  public void testDoGetAuthorizationInfo() throws Exception {
    PerryAccount perryAccount = perryAccount();
    String jwtToken = generateToken(perryAccount.getUser());
    List<Object> principals = Arrays.asList(perryAccount.getUser(), perryAccount, jwtToken);
    PrincipalCollection principalCollection =
            new SimplePrincipalCollection(principals, "testRealm");
    AuthorizationInfo authorizationInfo = jwtRealm.doGetAuthorizationInfo(principalCollection);
    Assert.assertEquals(perryAccount.getRoles(), authorizationInfo.getRoles());
  }

  @Test
  public void testDoGetAuthenticationInfo() throws Exception {
    String jwtToken = generateToken("testuser");
    AuthenticationToken token = new PerryShiroToken(jwtToken);
    AuthenticationInfo authenticationInfo = jwtRealm.doGetAuthenticationInfo(token);
    Assert.assertEquals(3, authenticationInfo.getPrincipals().asList().size());
    Assert.assertEquals(authenticationInfo.getPrincipals().asList().get(0), "testuser");
    PerryAccount perryAccount = (PerryAccount) authenticationInfo.getPrincipals().asList().get(1);
    Assert.assertEquals("testuser", perryAccount.getUser());
    Assert.assertEquals(new HashSet<>(Arrays.asList("role1", "role2")), perryAccount.getRoles());
    Assert.assertEquals(authenticationInfo.getPrincipals().asList().get(2), jwtToken);
  }

  private String generateToken(String subject) throws Exception {
    String identity = new String(Files.readAllBytes(Paths.get(getClass().getResource("/security/token.json").toURI())));
    return jwtRealm.jwtService.generate("id", subject, identity);
  }

  private PerryAccount perryAccount() {
    PerryAccount perryAccount = new PerryAccount();
    perryAccount.setUser("testuser");
    Set<String> roles = new HashSet<>(Arrays.asList("role1", "role2"));
    perryAccount.setRoles(roles);
    return perryAccount;
  }

  private static class JwtRealmMock extends JwtRealm {
    protected JwtConfiguration jwtConfiguration() {
      JwtConfiguration jwtConfiguration = super.jwtConfiguration();
      jwtConfiguration.setTimeout(5);
      return jwtConfiguration;
    }
  }
}
