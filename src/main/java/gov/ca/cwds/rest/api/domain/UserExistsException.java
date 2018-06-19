package gov.ca.cwds.rest.api.domain;

public class UserExistsException extends RuntimeException {

  private static final long serialVersionUID = 71714582833800531L;

  public UserExistsException(String message) {
    super(message);
  }

  public UserExistsException(String message, Throwable e) {
    super(message, e);
  }
}
