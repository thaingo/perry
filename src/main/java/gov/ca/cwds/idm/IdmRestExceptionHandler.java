package gov.ca.cwds.idm;

import gov.ca.cwds.idm.dto.IdmApiCustomError;
import gov.ca.cwds.rest.api.domain.IdmException;
import gov.ca.cwds.rest.api.domain.UserAlreadyExistsException;
import gov.ca.cwds.rest.api.domain.UserNotFoundPerryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {IdmResource.class, OfficesResource.class})
public class IdmRestExceptionHandler extends ResponseEntityExceptionHandler {

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

  private IdmApiCustomError buildApiCustomError(IdmException e, HttpStatus httpStatus) {
    return IdmApiCustomError.IdmApiCustomErrorBuilder.anIdmApiCustomError()
        .withStatus(httpStatus)
        .withErrorCode(e.getErrorCode())
        .withTechnicalMessage(e.getMessage())
        .withUserMessage(e.getUserMessage())
        .build();
  }
}
