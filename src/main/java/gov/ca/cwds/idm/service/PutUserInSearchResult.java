package gov.ca.cwds.idm.service;

import java.util.Optional;

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

  public Optional<Exception> getException() {
    return Optional.ofNullable(exception);
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }

  public Optional<UserLogResult> getUserLogResult() {
    return Optional.ofNullable(userLogResult);
  }

  public void setUserLogResult(UserLogResult userLogResult) {
    this.userLogResult = userLogResult;
  }
}
