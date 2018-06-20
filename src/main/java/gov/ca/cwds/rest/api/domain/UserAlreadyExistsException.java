package gov.ca.cwds.rest.api.domain;

public class UserAlreadyExistsException extends RuntimeException {

  private static final long serialVersionUID = 234671130524693850L;

  public UserAlreadyExistsException(String message) {
    super(message);
  }

  public UserAlreadyExistsException(String message, Throwable e) {
    super(message, e);
  }
}
