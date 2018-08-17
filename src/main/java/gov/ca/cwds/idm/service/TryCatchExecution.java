package gov.ca.cwds.idm.service;

import java.util.function.Consumer;

public class TryCatchExecution<T> {

  private ResultType resultType;
  private Exception exception;

  public TryCatchExecution(T input, Consumer<T> tryConsumer, Consumer<Exception> catchConsumer){
    try {
      tryConsumer.accept(input);
      resultType = ResultType.SUCCESS;

    } catch (Exception e) {
      resultType = ResultType.FAIL;
      exception = e;
      catchConsumer.accept(e);
    }
  }

  public ResultType getResultType() {
    return resultType;
  }

  public Exception getException() {
    return exception;
  }
}
