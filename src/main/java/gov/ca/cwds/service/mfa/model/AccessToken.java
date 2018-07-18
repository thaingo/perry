package gov.ca.cwds.service.mfa.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

  @JsonProperty("jwtToken")
  private String jwtToken;
  @JsonProperty("payload")
  private Payload payload;

  @JsonProperty("jwtToken")
  public String getJwtToken() {
    return jwtToken;
  }

  @JsonProperty("jwtToken")
  public void setJwtToken(String jwtToken) {
    this.jwtToken = jwtToken;
  }

  @JsonProperty("payload")
  public Payload getPayload() {
    return payload;
  }

  @JsonProperty("payload")
  public void setPayload(Payload payload) {
    this.payload = payload;
  }

}
