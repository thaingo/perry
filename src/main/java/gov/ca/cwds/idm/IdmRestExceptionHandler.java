package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.IdmResource.DATETIME_FORMAT_PATTERN;
import static gov.ca.cwds.service.messages.MessageCode.INVALID_DATE_FORMAT;

import gov.ca.cwds.idm.dto.IdmApiCustomError;
import gov.ca.cwds.rest.api.domain.IdmException;
import gov.ca.cwds.rest.api.domain.UserAlreadyExistsException;
import gov.ca.cwds.rest.api.domain.UserNotFoundPerryException;
import gov.ca.cwds.service.messages.MessagesService;
import java.time.format.DateTimeParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {IdmResource.class, OfficesResource.class})
public class IdmRestExceptionHandler extends ResponseEntityExceptionHandler {

  @Autowired
  private MessagesService messages;

  @ExceptionHandler(value = {UserNotFoundPerryException.class})
  protected ResponseEntity<Object> handleUserNotFound() {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(value = {UserAlreadyExistsException.class})
  protected ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
    HttpStatus httpStatus = HttpStatus.CONFLICT;
    IdmApiCustomError apiError = buildApiCustomError(e, httpStatus);
    return new ResponseEntity<>(apiError, httpStatus);
  }

  @ExceptionHandler(value = {DateTimeParseException.class})
  protected ResponseEntity<Object> handleDateTimeParseException(DateTimeParseException e) {
    String msg = messages.getTechMessage(INVALID_DATE_FORMAT, DATETIME_FORMAT_PATTERN);
    String userMessage = messages.getUserMessage(INVALID_DATE_FORMAT, DATETIME_FORMAT_PATTERN);
    logger.error(msg, e);
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    IdmApiCustomError apiError =
        IdmApiCustomError.IdmApiCustomErrorBuilder.anIdmApiCustomError()
            .withStatus(httpStatus)
            .withErrorCode(INVALID_DATE_FORMAT)
            .withTechnicalMessage(msg)
            .withUserMessage(userMessage)
            .build();
    return new ResponseEntity<>(apiError, httpStatus);
  }

  private IdmApiCustomError buildApiCustomError(IdmException e, HttpStatus httpStatus) {
    return IdmApiCustomError.IdmApiCustomErrorBuilder.anIdmApiCustomError()
        .withStatus(httpStatus)
        .withErrorCode(e.getErrorCode())
        .withTechnicalMessage(e.getMessage())
        .withUserMessage(e.getUserMessage())
        .build();
  }
}
