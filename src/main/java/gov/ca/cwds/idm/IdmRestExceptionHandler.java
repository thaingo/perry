package gov.ca.cwds.idm;

import gov.ca.cwds.rest.api.domain.UserNotFoundPerryException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {IdmResource.class, OfficesResource.class})
public class IdmRestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {UserNotFoundPerryException.class})
  protected ResponseEntity<Object> handleUserNotFoundPerryException(UserNotFoundPerryException ex) {
    return ResponseEntity.notFound().build();
  }
}
