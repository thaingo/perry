package gov.ca.cwds.idm.service;

public class PutUserInSearchResult {

  private ResultType resultType = ResultType.WAS_NOT_EXECUTED;
  private Exception exception;
  private UserLogResult userLogResult;

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

  public UserLogResult getUserLogResult() {
    return userLogResult;
  }

  public void setUserLogResult(UserLogResult userLogResult) {
    this.userLogResult = userLogResult;
  }
}
