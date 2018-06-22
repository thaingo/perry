package gov.ca.cwds.rest.api.domain;

public class UserValidationException extends RuntimeException {

  private static final long serialVersionUID = 3122648071078983603L;

  public UserValidationException(String message) {
    super(message);
  }

  public UserValidationException(String message, Throwable e) {
    super(message, e);
  }
}
