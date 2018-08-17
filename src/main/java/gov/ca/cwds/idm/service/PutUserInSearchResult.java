package gov.ca.cwds.idm.service;

public class PutUserInSearchResult {

  private ResultType resultType = ResultType.WAS_NOT_EXECUTED;
  private Exception exception;
  private TryCatchExecution userLogResult;

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

  public TryCatchExecution getUserLogResult() {
    return userLogResult;
  }

  public void setUserLogResult(TryCatchExecution userLogResult) {
    this.userLogResult = userLogResult;
  }
}
