package gov.ca.cwds.idm.service.cognito;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Profile;

@Profile("idm")
@ConfigurationProperties(prefix = "search")
public class SearchProperties {

  private String doraUrl;

  @NestedConfigurationProperty
  private SearchIndex usersIndex;

  @NestedConfigurationProperty
  private SearchIndex auditIndex;

  public String getDoraUrl() {
    return doraUrl;
  }

  public void setDoraUrl(String doraUrl) {
    this.doraUrl = doraUrl;
  }

  public SearchIndex getUsersIndex() {
    return usersIndex;
  }

  public void setUsersIndex(SearchIndex usersIndex) {
    this.usersIndex = usersIndex;
  }

  public SearchIndex getAuditIndex() {
    return auditIndex;
  }

  public void setAuditIndex(SearchIndex auditIndex) {
    this.auditIndex = auditIndex;
  }

  public static class SearchIndex {

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