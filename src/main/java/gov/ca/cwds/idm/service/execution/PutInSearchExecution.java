package gov.ca.cwds.idm.service.execution;

import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import org.springframework.http.ResponseEntity;

public abstract class PutInSearchExecution<T> extends
    OptionalExecution<T, ResponseEntity<String>> {

  private OptionalExecution<String, UserLog> userLogExecution;

  public PutInSearchExecution(T input) {
    super(input);
  }

  public OptionalExecution<String, UserLog> getUserLogExecution() {
    return userLogExecution;
  }

  public void setUserLogExecution(
      OptionalExecution<String, UserLog> userLogExecution) {
    this.userLogExecution = userLogExecution;
  }
}
