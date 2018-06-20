package gov.ca.cwds.idm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("idm")
@ConfigurationProperties(prefix = "cognito")
public class CognitoProperties {
  private String iamAccessKeyId;
  private String iamSecretKey;
  private String region;
  private String userpool;

  public String getIamAccessKeyId() {
    return iamAccessKeyId;
  }

  public void setIamAccessKeyId(String iamAccessKeyId) {
    this.iamAccessKeyId = iamAccessKeyId;
  }

  public String getIamSecretKey() {
    return iamSecretKey;
  }

  public void setIamSecretKey(String iamSecretKey) {
    this.iamSecretKey = iamSecretKey;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getUserpool() {
    return userpool;
  }

  public void setUserpool(String userpool) {
    this.userpool = userpool;
  }
}
