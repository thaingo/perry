package gov.ca.cwds.idm.service;

public abstract class TryCatchExecution<T> {

  private ResultType resultType;
  private Exception exception;

  public TryCatchExecution(T input){
    try {
      tryMethod(input);
      resultType = ResultType.SUCCESS;

    } catch (Exception e) {
      resultType = ResultType.FAIL;
      exception = e;
      catchMethod(e);
    }
  }

  protected abstract void tryMethod(T input);

  protected abstract void catchMethod(Exception e);

  public ResultType getResultType() {
    return resultType;
  }

  public Exception getException() {
    return exception;
  }
}
