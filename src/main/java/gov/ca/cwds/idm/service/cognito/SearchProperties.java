package gov.ca.cwds.idm.service.cognito;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Profile;

@Profile("idm")
@ConfigurationProperties(prefix = "search")
public class SearchProperties {

  private String doraUrl;

  private String doraBasicAuthUser;

  private String doraBasicAuthPass;

  @NestedConfigurationProperty
  private SearchIndexProperties usersIndex;

  @NestedConfigurationProperty
  private SearchIndexProperties auditIndex;

  public String getDoraUrl() {
    return doraUrl;
  }

  public void setDoraUrl(String doraUrl) {
    this.doraUrl = doraUrl;
  }

  public String getDoraBasicAuthUser() {
    return doraBasicAuthUser;
  }

  public void setDoraBasicAuthUser(String doraBasicAuthUser) {
    this.doraBasicAuthUser = doraBasicAuthUser;
  }

  public String getDoraBasicAuthPass() {
    return doraBasicAuthPass;
  }

  public void setDoraBasicAuthPass(String doraBasicAuthPass) {
    this.doraBasicAuthPass = doraBasicAuthPass;
  }

  public SearchIndexProperties getUsersIndex() {
    return usersIndex;
  }

  public void setUsersIndex(SearchIndexProperties usersIndex) {
    this.usersIndex = usersIndex;
  }

  public SearchIndexProperties getAuditIndex() {
    return auditIndex;
  }

  public void setAuditIndex(SearchIndexProperties auditIndex) {
    this.auditIndex = auditIndex;
  }

  public static class SearchIndexProperties {

    private String name;
    private String type;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}