package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.IdmResource.getNewUserLocationUri;
import static gov.ca.cwds.idm.persistence.ns.OperationType.CREATE;
import static gov.ca.cwds.service.messages.MessageCode.INVALID_DATE_FORMAT;
import static gov.ca.cwds.util.Utils.URL_DATETIME_FORMAT_PATTERN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import gov.ca.cwds.idm.dto.IdmApiCustomError;
import gov.ca.cwds.idm.dto.IdmApiCustomError.IdmApiCustomErrorBuilder;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.exception.IdmException;
import gov.ca.cwds.idm.exception.PartialSuccessException;
import gov.ca.cwds.idm.exception.UserAlreadyExistsException;
import gov.ca.cwds.idm.exception.UserNotFoundException;
import gov.ca.cwds.idm.exception.UserValidationException;
import gov.ca.cwds.service.messages.MessagesService;
import gov.ca.cwds.service.messages.MessagesService.Messages;
import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Profile("idm")
@ControllerAdvice(assignableTypes = {IdmResource.class})
public class IdmRestExceptionHandler extends ResponseEntityExceptionHandler {


  @Autowired
  private MessagesService messagesService;

  @ExceptionHandler(value = {IdmException.class})
  ResponseEntity<Object> handleIdmException(IdmException e) {
    logger.error(e);
    return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e);
  }

  @ExceptionHandler(value = {UserNotFoundException.class})
  ResponseEntity<Object> handleUserNotFound(UserNotFoundException e) {
    logger.error(e);
    return buildResponseEntity(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(value = {UserAlreadyExistsException.class})
  ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
    logger.error(e);
    return buildResponseEntity(HttpStatus.CONFLICT, e);
  }

  @ExceptionHandler(value = {UserValidationException.class})
  ResponseEntity<Object> handleUserValidationException(UserValidationException e) {
    logger.error(e);
    return buildResponseEntity(HttpStatus.BAD_REQUEST, e);
  }

  @ExceptionHandler(value = {AdminAuthorizationException.class})
  ResponseEntity<Object> handleAdminAuthorizationException(AdminAuthorizationException e) {
    logger.error(e);
    return buildResponseEntity(HttpStatus.UNAUTHORIZED, e);
  }

  @ExceptionHandler(value = {PartialSuccessException.class})
  ResponseEntity<Object> handlePartialSuccess(PartialSuccessException e) {
    logger.error(e);

    HttpStatus httpStatus = INTERNAL_SERVER_ERROR;
    List<Exception> causes = e.getCauses();

    if (e.getOperationType() == CREATE) {
      URI locationUri = getNewUserLocationUri(e.getUserId());
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(locationUri);
      return buildResponseEntity(httpStatus, e, causes, headers);
    } else {
      return buildResponseEntity(httpStatus, e, causes);
    }
  }

  @ExceptionHandler(value = {DateTimeParseException.class})
  ResponseEntity<Object> handleDateTimeParseException(DateTimeParseException e) {

    Messages messages = messagesService
        .getMessages(INVALID_DATE_FORMAT, URL_DATETIME_FORMAT_PATTERN);
    logger.error(messages.getTechMsg(), e);

    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    IdmApiCustomError apiError =
        IdmApiCustomError.IdmApiCustomErrorBuilder.anIdmApiCustomError()
            .withStatus(httpStatus)
            .withErrorCode(INVALID_DATE_FORMAT)
            .withTechnicalMessage(messages.getTechMsg())
            .withUserMessage(messages.getUserMsg())
            .build();
    return new ResponseEntity<>(apiError, httpStatus);
  }

  private ResponseEntity<Object> buildResponseEntity(HttpStatus httpStatus, IdmException e) {
    return new ResponseEntity<>(buildApiCustomError(httpStatus, e), httpStatus);
  }

  private ResponseEntity<Object> buildResponseEntity(HttpStatus httpStatus, IdmException e,
      List<Exception> causes) {
    return new ResponseEntity<>(buildApiCustomError(httpStatus, e, causes), httpStatus);
  }

  private ResponseEntity<Object> buildResponseEntity(HttpStatus httpStatus, IdmException e,
      List<Exception> causes, HttpHeaders headers) {
    return new ResponseEntity<>(buildApiCustomError(httpStatus, e, causes), headers, httpStatus);
  }

  private IdmApiCustomError buildApiCustomError(HttpStatus httpStatus, IdmException e) {
    return createErrorBuilder(e, httpStatus).build();
  }

  private IdmApiCustomError buildApiCustomError(HttpStatus httpStatus, IdmException e,
      List<Exception> causes) {
    return createErrorBuilder(e, httpStatus).withCauses(causes).build();
  }

  private IdmApiCustomErrorBuilder createErrorBuilder(IdmException e, HttpStatus httpStatus) {
    return IdmApiCustomErrorBuilder.anIdmApiCustomError()
        .withStatus(httpStatus)
        .withErrorCode(e.getErrorCode())
        .withTechnicalMessage(e.getMessage())
        .withUserMessage(e.getUserMessage())
        .withCause(e.getCause());
  }
}
