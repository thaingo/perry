package gov.ca.cwds.service.mfa.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payload {

  @JsonProperty("exp")
  private Integer exp;

  @JsonProperty("exp")
  public Integer getExp() {
    return exp;
  }

  @JsonProperty("exp")
  public void setExp(Integer exp) {
    this.exp = exp;
  }

}
