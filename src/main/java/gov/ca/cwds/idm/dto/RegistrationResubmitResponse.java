package gov.ca.cwds.idm.dto;

import static gov.ca.cwds.util.Utils.DATE_TIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@SuppressWarnings("squid:S3437")
public class RegistrationResubmitResponse implements Serializable {

  private static final long serialVersionUID = 6068687985325199042L;

  @NotBlank
  private final String userId;

  @NotBlank
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
  private final LocalDateTime lastRegistrationResubmitDateTime;

  public RegistrationResubmitResponse(
      String userId,
      LocalDateTime lastRegistrationResubmitDateTime) {
    this.userId = userId;
    this.lastRegistrationResubmitDateTime = lastRegistrationResubmitDateTime;
  }

  public String getUserId() {
    return userId;
  }

  public LocalDateTime getLastRegistrationResubmitDateTime() {
    return lastRegistrationResubmitDateTime;
  }
}
