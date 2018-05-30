package gov.ca.cwds.rest.api.domain;

public class UserNotFoundPerryException extends RuntimeException {

  private static final long serialVersionUID = -9155746523212710138L;

  public UserNotFoundPerryException(String message) {
    super(message);
  }

  public UserNotFoundPerryException(String message, Throwable e) {
    super(message, e);
  }
}
