package gov.ca.cwds.idm.service.cognito;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("idm")
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {

  private String doraUrl;
  private String index;
  private String type;

  public String getDoraUrl() {
    return doraUrl;
  }

  public void setDoraUrl(String doraUrl) {
    this.doraUrl = doraUrl;
  }

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}