package gov.ca.cwds.idm.service;

public class UserLogResult {
  private ResultType resultType = ResultType.WAS_NOT_EXECUTED;
  private Exception exception;

  public ResultType getResultType() {
    return resultType;
  }

  public void setResultType(ResultType resultType) {
    this.resultType = resultType;
  }

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }
}
