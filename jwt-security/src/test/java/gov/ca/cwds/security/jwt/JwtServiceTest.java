package gov.ca.cwds.security.jwt;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by dmitry.rudenko on 6/30/2017.
 */
@Ignore
public class JwtServiceTest {

  private JwtConfiguration configuration;

  @Before
  public void before() throws Exception {
    configuration = new JwtConfiguration();
    //JWT
    configuration.setTimeout(30);
    configuration.setIssuer("issuer");
    configuration.setKeyStore(new JwtConfiguration.KeyStoreConfiguration());
    //KeyStore
    configuration.getKeyStore().setPath(getClass().getResource("/security/enc.jceks").getPath());
    configuration.getKeyStore().setPassword("test");
    //Sign/Validate Key
    configuration.getKeyStore().setAlias("test");
    configuration.getKeyStore().setKeyPassword("test");
    //Enc Key
    configuration.setEncryptionEnabled(true);
    configuration.getKeyStore().setEncKeyPassword("test");
    configuration.getKeyStore().setEncAlias("enc128");
    configuration.setEncryptionMethod("A128GCM");

  }

  @Test
  public void test() throws Exception {
    JwtService jwtService = new JwtService(configuration);
    String token = jwtService.generate("id", "subject", "identity");
    String identity = jwtService.validate(token);
    Assert.assertEquals("identity", identity);
  }


  @Test(expected = JwtException.class)
  public void testExpired() throws Exception {
    configuration.setTimeout(-1);
    JwtService jwtService = new JwtService(configuration);
    String token = jwtService.generate("id", "subject", "identity");
    jwtService.validate(token);
  }

  @Test(expected = JwtException.class)
  public void testIssuerValidation() throws Exception {
    JwtService jwtService = new JwtService(configuration);
    String token = jwtService.generate("id", "subject", "identity");
    configuration.setIssuer("not valid");
    jwtService.validate(token);
  }

  @Test(expected = JwtException.class)
  public void testEncryption() throws Exception {
    JwtService jwtService = new JwtService(configuration);
    String token = jwtService.generate("id", "subject", "identity");
    StringBuilder sb = new StringBuilder(token);
    sb.setCharAt(sb.lastIndexOf(".") - 5, '1');
    jwtService.validate(sb.toString());
  }

  @Test
  public void testSignatureOnly() throws Exception {
    configuration.setEncryptionEnabled(false);
    JwtService jwtService = new JwtService(configuration);
    String token = jwtService.generate("id", "subject", "identity");
    String identity = jwtService.validate(token);
    Assert.assertEquals("identity", identity);
  }

  @Test(expected = JwtException.class)
  public void testSignatureValidation() throws Exception {
    configuration.setEncryptionEnabled(false);
    JwtService jwtService = new JwtService(configuration);
    String token = jwtService.generate("id", "subject", "identity");
    System.out.println(token);
    StringBuilder sb = new StringBuilder(token);
    sb.setCharAt(sb.lastIndexOf(".") - 5, '1');
    jwtService.validate(sb.toString());
  }


}
