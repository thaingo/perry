package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.persistence.model.UserLog;
import java.util.Optional;

public class UserLogResult {
  private ResultType resultType = ResultType.WAS_NOT_EXECUTED;
  private Optional<UserLog> userLog;
  private Optional<Exception> exception;

  public ResultType getResultType() {
    return resultType;
  }

  public void setResultType(ResultType resultType) {
    this.resultType = resultType;
  }

  public Optional<UserLog> getUserLog() {
    return userLog;
  }

  public void setUserLog(Optional<UserLog> userLog) {
    this.userLog = userLog;
  }

  public Optional<Exception> getException() {
    return exception;
  }

  public void setException(Optional<Exception> exception) {
    this.exception = exception;
  }
}
