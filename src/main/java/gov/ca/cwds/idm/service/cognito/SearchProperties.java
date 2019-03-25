package gov.ca.cwds.idm.service.cognito;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Profile;

@Profile("idm")
@ConfigurationProperties(prefix = "search")
public class SearchProperties {

  private String doraUrl;

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