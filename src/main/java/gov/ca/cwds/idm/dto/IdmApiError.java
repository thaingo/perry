package gov.ca.cwds.idm.dto;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.exception.IdmException;
import gov.ca.cwds.idm.exception.PartialSuccessException;
import gov.ca.cwds.service.messages.Messages;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IdmApiError {

  @JsonProperty
  private String errorCode;

  @JsonProperty
  private String technicalMessage;

  @JsonProperty
  private String userMessage;

  @JsonProperty
  private List<String> causes;

  private IdmApiError(String errorCode, String technicalMessage, String userMessage) {
    this.errorCode = errorCode;
    this.technicalMessage = technicalMessage;
    this.userMessage = userMessage;
    this.causes = new ArrayList<>();
  }

  private IdmApiError(IdmException e) {
    this(e.getErrorCode().getValue(), e.getMessage(), e.getUserMessage());
  }

  public static IdmApiError createIdmApiError(Messages messages) {
    return new IdmApiError(messages.getMessageCode().getValue(), messages.getTechMsg(),
        messages.getUserMsg());
  }

  public static IdmApiError createIdmApiError(IdmException e) {
    IdmApiError idmApiError = new IdmApiError(e);

    Throwable cause = e.getCause();
    if (cause != null && cause.getMessage() != null) {
      idmApiError.addCause(cause.getMessage());
    }
    return idmApiError;
  }

  public static IdmApiError createPartialSuccessIdmApiError(PartialSuccessException e) {
    IdmApiError idmApiError = new IdmApiError(e);
    idmApiError
        .addCauses(e.getCauses().stream().map(IdmApiError::getMessageWithCause).collect(toList()));
    return idmApiError;
  }

  private static String getMessageWithCause(Exception e) {
    String result = e.getMessage();
    Throwable cause = e.getCause();
    if (cause != null && cause.getMessage() != null) {
      result += ": " + cause.getMessage();
    }
    return result;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getTechnicalMessage() {
    return technicalMessage;
  }

  public String getUserMessage() {
    return userMessage;
  }

  public List<String> getCauses() {
    return causes;
  }

  private void addCause(String cause) {
    this.causes.add(cause);
  }

  private void addCauses(List<String> causes) {
    this.causes.addAll(causes);
  }
}
