package gov.ca.cwds.idm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class IdmApiCustomError {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;

  @JsonProperty
  private HttpStatus status;

  @JsonProperty
  private String message;

  private IdmApiCustomError() {
    timestamp = LocalDateTime.now();
  }

  IdmApiCustomError(HttpStatus status) {
    this();
    this.status = status;
  }

  IdmApiCustomError(HttpStatus status, String message) {
    this();
    this.status = status;
    this.message = message;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
