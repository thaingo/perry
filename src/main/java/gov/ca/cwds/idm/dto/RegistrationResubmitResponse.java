package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import org.hibernate.validator.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@SuppressWarnings("squid:S3437")
public class RegistrationResubmitResponse implements Serializable {

  private static final long serialVersionUID = 8880750675080969398L;

  @NotBlank
  private final String userId;

  @JsonCreator
  public RegistrationResubmitResponse(
      @JsonProperty("user_id")
      String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

}
