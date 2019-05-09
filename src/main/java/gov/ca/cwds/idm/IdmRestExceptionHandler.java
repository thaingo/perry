package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.IdmResource.getNewUserLocationUri;
import static gov.ca.cwds.idm.dto.IdmApiErrorResponse.createIdmApiErrorResponse;
import static gov.ca.cwds.idm.dto.IdmApiErrorResponse.createPartialSuccessIdmApiErrorResponse;
import static gov.ca.cwds.idm.persistence.ns.OperationType.CREATE;
import static gov.ca.cwds.service.messages.MessageCode.INVALID_DATE_FORMAT;
import static gov.ca.cwds.util.Utils.URL_DATETIME_FORMAT_PATTERN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.exception.IdmException;
import gov.ca.cwds.idm.exception.OperationNotSupportedException;
import gov.ca.cwds.idm.exception.PartialSuccessException;
import gov.ca.cwds.idm.exception.UserAlreadyExistsException;
import gov.ca.cwds.idm.exception.UserNotFoundException;
import gov.ca.cwds.idm.exception.UserValidationException;
import gov.ca.cwds.service.messages.Messages;
import gov.ca.cwds.service.messages.MessagesService;
import java.net.URI;
import java.time.format.DateTimeParseException;
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
@SuppressWarnings({"fb-contrib:LO_STUTTERED_MESSAGE"})//IdmExceptions contain our custom messages
public class IdmRestExceptionHandler extends ResponseEntityExceptionHandler {

  @Autowired
  private MessagesService messagesService;

  @ExceptionHandler(value = {IdmException.class})
  ResponseEntity<Object> handleIdmException(IdmException e) {
    logger.error(e.getMessage(), e);
    return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e);
  }

  @ExceptionHandler(value = {UserNotFoundException.class})
  ResponseEntity<Object> handleUserNotFound(UserNotFoundException e) {
    logger.error(e.getMessage(), e);
    return buildResponseEntity(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(value = {UserAlreadyExistsException.class})
  ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
    logger.error(e.getMessage(), e);
    return buildResponseEntity(HttpStatus.CONFLICT, e);
  }

  @ExceptionHandler(value = {UserValidationException.class})
  ResponseEntity<Object> handleUserValidationException(UserValidationException e) {
    logger.error(e.getMessage(), e);
    return buildResponseEntity(HttpStatus.BAD_REQUEST, e);
  }

  @ExceptionHandler(value = {OperationNotSupportedException.class})
  ResponseEntity<Object> handleOperationNotSupportedException(OperationNotSupportedException e) {
    logger.error(e.getMessage(), e);
    return buildResponseEntity(HttpStatus.BAD_REQUEST, e);
  }

  @ExceptionHandler(value = {AdminAuthorizationException.class})
  ResponseEntity<Object> handleAdminAuthorizationException(AdminAuthorizationException e) {
    logger.error(e.getMessage(), e);
    return buildResponseEntity(HttpStatus.UNAUTHORIZED, e);
  }

  @ExceptionHandler(value = {PartialSuccessException.class})
  ResponseEntity<Object> handlePartialSuccess(PartialSuccessException e) {
    logger.error(e.getMessage(), e);

    HttpStatus httpStatus = INTERNAL_SERVER_ERROR;

    if (e.getOperationType() == CREATE) {
      URI locationUri = getNewUserLocationUri(e.getUserId());
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(locationUri);
      return buildPartialSuccessResponseEntity(httpStatus, e, headers);
    } else {
      return buildPartialSuccessResponseEntity(httpStatus, e);
    }
  }

  @ExceptionHandler(value = {DateTimeParseException.class})
  ResponseEntity<Object> handleDateTimeParseException(DateTimeParseException e) {
    Messages messages = messagesService
        .getMessages(INVALID_DATE_FORMAT, URL_DATETIME_FORMAT_PATTERN);
    logger.error(messages.getTechMsg(), e);
    return buildResponseEntity(HttpStatus.BAD_REQUEST, messages);
  }

  private ResponseEntity<Object> buildResponseEntity(HttpStatus httpStatus, Messages messages) {
    return new ResponseEntity<>(createIdmApiErrorResponse(httpStatus, messages) , httpStatus);
  }


  private ResponseEntity<Object> buildResponseEntity(HttpStatus httpStatus, IdmException e) {
    return new ResponseEntity<>(createIdmApiErrorResponse(httpStatus, e) , httpStatus);
  }

  private ResponseEntity<Object> buildPartialSuccessResponseEntity(
      HttpStatus httpStatus, PartialSuccessException e) {
    return new ResponseEntity<>(createPartialSuccessIdmApiErrorResponse(httpStatus, e), httpStatus);
  }

  private ResponseEntity<Object> buildPartialSuccessResponseEntity(HttpStatus httpStatus,
      PartialSuccessException e, HttpHeaders headers) {
    return new ResponseEntity<>(createPartialSuccessIdmApiErrorResponse(httpStatus, e), headers,
        httpStatus);
  }
}
