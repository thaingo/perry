package gov.ca.cwds.rest.api.domain;

public class UserIdmValidationException extends RuntimeException {

  private static final long serialVersionUID = 3122648071078983603L;

  public UserIdmValidationException(String message) {
    super(message);
  }

  public UserIdmValidationException(String message, Throwable e) {
    super(message, e);
  }
}
