package gov.ca.cwds.idm.dto;

import static gov.ca.cwds.config.LoggingRequestIdFilter.REQUEST_ID;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.service.messages.MessageCode;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@JsonInclude(Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@SuppressWarnings({"squid:S3437"})
public class IdmApiCustomError  implements Serializable {

  private static final long serialVersionUID = -7854227531434018227L;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;

  @JsonProperty
  private HttpStatus status;

  @JsonProperty
  private String technicalMessage;

  @JsonProperty
  private String userMessage;

  @JsonProperty
  private String errorCode;

  @JsonProperty
  private String incidentId;

  @JsonProperty
  private List<String> causes = new ArrayList<>();

  private IdmApiCustomError() {
    timestamp = LocalDateTime.now();
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getTechnicalMessage() {
    return technicalMessage;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getUserMessage() {
    return userMessage;
  }

  public String getIncidentId() {
    return incidentId;
  }

  public List<String> getCauses() {
    return causes;
  }


  public static final class IdmApiCustomErrorBuilder {

    private HttpStatus status;
    private String technicalMessage;
    private String userMessage;
    private String errorCode;
    private List<String> causes = new ArrayList<>();

    private IdmApiCustomErrorBuilder() {
    }

    public static IdmApiCustomErrorBuilder anIdmApiCustomError() {
      return new IdmApiCustomErrorBuilder();
    }

    public IdmApiCustomErrorBuilder withStatus(HttpStatus status) {
      this.status = status;
      return this;
    }

    public IdmApiCustomErrorBuilder withTechnicalMessage(String technicalMessage) {
      this.technicalMessage = technicalMessage;
      return this;
    }

    public IdmApiCustomErrorBuilder withUserMessage(String userMessage) {
      this.userMessage = userMessage;
      return this;
    }

    public IdmApiCustomErrorBuilder withErrorCode(MessageCode errorCode) {
      this.errorCode = errorCode.getValue();
      return this;
    }

    public IdmApiCustomErrorBuilder withCauses(List<Exception> causes) {
      this.causes.addAll(causes.stream().map(this::getMessageWithCause).collect(toList()));
      return this;
    }

    private String getMessageWithCause(Exception e) {
      StringBuffer result = new StringBuffer();
      result.append(e.getMessage());
      Throwable cause = e.getCause();
      if (cause != null && cause.getMessage() != null) {
        result.append(": ");
        result.append(cause.getMessage());
      }
      return result.toString();
    }

    public IdmApiCustomErrorBuilder withCause(Throwable cause) {
      if(cause != null && cause.getMessage() != null) {
        this.causes.add(cause.getMessage());
      }
      return this;
    }

    public IdmApiCustomError build() {
      IdmApiCustomError idmApiCustomError = new IdmApiCustomError();
      idmApiCustomError.incidentId = MDC.get(REQUEST_ID);
      idmApiCustomError.status = this.status;
      idmApiCustomError.errorCode = this.errorCode;
      idmApiCustomError.technicalMessage = this.technicalMessage;
      idmApiCustomError.userMessage = this.userMessage;
      idmApiCustomError.causes = this.causes;
      return idmApiCustomError;
    }
  }
}
