package gov.ca.cwds.idm.dto;

import static gov.ca.cwds.config.LoggingRequestIdFilter.REQUEST_ID;
import static gov.ca.cwds.idm.dto.IdmApiError.createIdmApiError;
import static gov.ca.cwds.idm.dto.IdmApiError.createPartialSuccessIdmApiError;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.exception.IdmException;
import gov.ca.cwds.idm.exception.PartialSuccessException;
import gov.ca.cwds.service.messages.Messages;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@JsonInclude(Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@SuppressWarnings({"squid:S3437"})
public class IdmApiErrorResponse implements Serializable {

  private static final long serialVersionUID = -7854227531434018227L;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private final LocalDateTime timestamp;

  @JsonProperty
  private final HttpStatus status;

  @JsonProperty
  private final String incidentId;

  @JsonProperty
  private List<IdmApiError> errors;

  private IdmApiErrorResponse(HttpStatus status) {
    this.status = status;
    timestamp = LocalDateTime.now();
    incidentId = MDC.get(REQUEST_ID);
    errors = new ArrayList<>();
  }

//  public IdmApiErrorResponse(HttpStatus status, IdmException idmException) {
//    this(status);
//    addIdmException(idmException);
//  }

  public static IdmApiErrorResponse createIdmApiErrorResponse(HttpStatus httpStatus, Messages messages) {
    return new IdmApiErrorResponse(httpStatus).addError(messages);
  }

  public static IdmApiErrorResponse createIdmApiErrorResponse(HttpStatus httpStatus, IdmException e) {
    return new IdmApiErrorResponse(httpStatus).addError(e);
  }

  public static IdmApiErrorResponse createPartialSuccessIdmApiErrorResponse(HttpStatus httpStatus,
      PartialSuccessException e) {
    return new IdmApiErrorResponse(httpStatus).addPartialSuccessError(e);
  }

  private IdmApiErrorResponse addError(Messages messages) {
    errors.add(createIdmApiError(messages));
    return this;
  }

  private IdmApiErrorResponse addError(IdmException idmException) {
    errors.add(createIdmApiError(idmException));
    return this;
  }

  private IdmApiErrorResponse addPartialSuccessError(PartialSuccessException partialSuccessException) {
    errors.add(createPartialSuccessIdmApiError(partialSuccessException));
    return this;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getIncidentId() {
    return incidentId;
  }


//  public static final class IdmApiErrorResponseBuilder {
//
//    private HttpStatus status;
////    private String technicalMessage;
////    private String userMessage;
////    private String errorCode;
////    private List<String> causes = new ArrayList<>();
//
//    private IdmApiErrorResponseBuilder() {
//    }
//
//    public static IdmApiErrorResponseBuilder anIdmApiCustomError() {
//      return new IdmApiErrorResponseBuilder();
//    }
//
//    public IdmApiErrorResponseBuilder withStatus(HttpStatus status) {
//      this.status = status;
//      return this;
//    }
//
////    public IdmApiErrorResponseBuilder withTechnicalMessage(String technicalMessage) {
////      this.technicalMessage = technicalMessage;
////      return this;
////    }
//
////    public IdmApiErrorResponseBuilder withUserMessage(String userMessage) {
////      this.userMessage = userMessage;
////      return this;
////    }
//
////    public IdmApiErrorResponseBuilder withErrorCode(MessageCode errorCode) {
////      this.errorCode = errorCode.getValue();
////      return this;
////    }
//
////    public IdmApiErrorResponseBuilder withCauses(List<Exception> causes) {
////      this.causes.addAll(causes.stream().map(this::getMessageWithCause).collect(toList()));
////      return this;
////    }
//
////    private String getMessageWithCause(Exception e) {
////      String result = e.getMessage();
////      Throwable cause = e.getCause();
////      if (cause != null && cause.getMessage() != null) {
////        result += ": " + cause.getMessage();
////      }
////      return result;
////    }
//
////    public IdmApiErrorResponseBuilder withCause(Throwable cause) {
////      if(cause != null && cause.getMessage() != null) {
////        this.causes.add(cause.getMessage());
////      }
////      return this;
////    }
//
//    public IdmApiErrorResponse build() {
//      IdmApiErrorResponse idmApiCustomError = new IdmApiErrorResponse();
//      idmApiCustomError.incidentId = MDC.get(REQUEST_ID);
//      idmApiCustomError.status = this.status;
////      idmApiCustomError.errorCode = this.errorCode;
////      idmApiCustomError.technicalMessage = this.technicalMessage;
////      idmApiCustomError.userMessage = this.userMessage;
////      idmApiCustomError.causes = this.causes;
//      return idmApiCustomError;
//    }
//  }
}
