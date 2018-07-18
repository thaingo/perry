package gov.ca.cwds.rest.api.domain;

import org.springframework.security.access.AccessDeniedException;

/**
 * Created by dmitry.rudenko on 9/14/2017.
 */
public class PerryException extends AccessDeniedException {

  public PerryException(String message) {
    super(message);
  }

  public PerryException(String message, Throwable e) {
    super(message + ": " + e.getMessage(), e);
  }

}
