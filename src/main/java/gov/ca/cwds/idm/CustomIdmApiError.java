package gov.ca.cwds.idm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class CustomIdmApiError {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;

  @JsonProperty
  private HttpStatus status;

  @JsonProperty
  private String message;

  private CustomIdmApiError() {
    timestamp = LocalDateTime.now();
  }

  CustomIdmApiError(HttpStatus status) {
    this();
    this.status = status;
  }

  CustomIdmApiError(HttpStatus status, String message) {
    this();
    this.status = status;
    this.message = message;
  }
}
