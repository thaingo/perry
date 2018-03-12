package gov.ca.cwds.security.realm;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authc.AuthenticationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author CWDS TPT-2 Team
 */
public class AbstractRealmTest {

  @Test
  public void realmMapTest() throws Exception {
    AbstractRealm realm = new AbstractRealm() {
      @Override
      protected String validate(String token) throws AuthenticationException {
        return null;
      }
    };
    realm.onInit();

    String token = new String(
        Files.readAllBytes(Paths.get(getClass().getResource("/security/token.json").toURI())));
    PerryAccount perryAccount = realm.map(token);
    Assert.assertEquals("testuser", perryAccount.getUser());
    Assert.assertTrue(perryAccount.getRoles() != null && perryAccount.getRoles().size() == 2);

    String tokenExt = new String(
        Files.readAllBytes(
            Paths.get(getClass().getResource("/security/token-extended.json").toURI())));
    PerryAccount perryAccountExt = realm.map(tokenExt);
    Assert.assertEquals("RACFID", perryAccountExt.getUser());
    Assert.assertEquals("34", perryAccountExt.getStaffId());
    Assert.assertTrue(perryAccountExt.getRoles() != null && perryAccountExt.getRoles().size() == 1);
    Assert.assertEquals("19", perryAccountExt.getCountyCode());
    Assert.assertEquals("1086", perryAccountExt.getCountyCwsCode());
    Assert.assertEquals("Los Angeles", perryAccountExt.getCountyName());
    Assert.assertTrue(
        perryAccountExt.getPrivileges() != null && perryAccountExt.getPrivileges().size() == 2);
  }

  public static class TestPerryAccount extends PerryAccount {
    @JsonProperty
    private String user;
    @JsonProperty
    private Set<String> roles;
    @JsonProperty
    private String staffId;
    @JsonProperty("county_code")
    private String countyCode;
    @JsonProperty("county_cws_code")
    private String countyCwsCode;
    @JsonProperty("county_name")
    private String countyName;
    @JsonProperty
    private Set<String> privileges;

    public TestPerryAccount(String user) {
      super(user);
    }

    /**
     * Default Constructor
     */
    public TestPerryAccount() {
    }

    public String getUser() {
      return user;
    }

    public void setUser(String user) {
      this.user = user;
    }

    public Set<String> getRoles() {
      return roles;
    }

    public void setRoles(Set<String> roles) {
      this.roles = roles;
    }

    public String getStaffId() {
      return staffId;
    }

    public void setStaffId(String staffId) {
      this.staffId = staffId;
    }

    public String getCountyCode() {
      return countyCode;
    }

    public void setCountyCode(String countyCode) {
      this.countyCode = countyCode;
    }

    @Override
    public String getCountyCwsCode() {
      return countyCwsCode;
    }

    @Override
    public void setCountyCwsCode(String countyCwsCode) {
      this.countyCwsCode = countyCwsCode;
    }

    public String getCountyName() {
      return countyName;
    }

    public void setCountyName(String countyName) {
      this.countyName = countyName;
    }

    public Set<String> getPrivileges() {
      return privileges;
    }

    public void setPrivileges(Set<String> privileges) {
      this.privileges = privileges;
    }
  }


  @Test
  public void backwardCompatibilityTest() throws Exception {
    AbstractRealm realm = new AbstractRealm() {
      private ObjectMapper objectMapper = new ObjectMapper();
      @Override
      protected String validate(String token) throws AuthenticationException {
        return null;
      }

      protected TestPerryAccount map(String json) {
        try {
          return objectMapper.readValue(json, TestPerryAccount.class);
        } catch (Exception e) {
          return new TestPerryAccount(json);
        }
      }
    };
    realm.onInit();

    String token = new String(
            Files.readAllBytes(Paths.get(getClass().getResource("/security/token.json").toURI())));
    PerryAccount perryAccount = realm.map(token);
    Assert.assertEquals("testuser", perryAccount.getUser());
    Assert.assertTrue(perryAccount.getRoles() != null && perryAccount.getRoles().size() == 2);

    String tokenExt = new String(
            Files.readAllBytes(
                    Paths.get(getClass().getResource("/security/token-extended.json").toURI())));
    PerryAccount perryAccountExt = realm.map(tokenExt);
    Assert.assertEquals("RACFID", perryAccountExt.getUser());
    Assert.assertEquals("34", perryAccountExt.getStaffId());
    Assert.assertTrue(perryAccountExt.getRoles() != null && perryAccountExt.getRoles().size() == 1);
    Assert.assertEquals("19", perryAccountExt.getCountyCode());
    Assert.assertEquals("1086", perryAccountExt.getCountyCwsCode());
    Assert.assertEquals("Los Angeles", perryAccountExt.getCountyName());
    Assert.assertTrue(
            perryAccountExt.getPrivileges() != null && perryAccountExt.getPrivileges().size() == 2);
  }

}