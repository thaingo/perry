package gov.ca.cwds.idm.service.execution;

import gov.ca.cwds.idm.service.OperationResultType;

public abstract class OptionalExecution<T, R> {

  private OperationResultType resultType;
  private Exception exception;
  private R response;

  @SuppressWarnings({"fb-contrib:PCOA_PARTIALLY_CONSTRUCTED_OBJECT_ACCESS"})
  //implementations of tryMethod() should not use resultType field by design
  public OptionalExecution(T input){
    try {
      response = tryMethod(input);
      resultType = OperationResultType.SUCCESS;

    } catch (Exception e) {
      resultType = OperationResultType.FAIL;
      exception = e;
      catchMethod(e);
    }
  }

  protected abstract R tryMethod(T input);

  protected abstract void catchMethod(Exception e);

  public OperationResultType getResultType() {
    return resultType;
  }

  public Exception getException() {
    return exception;
  }

  public R getResponse() {
    return response;
  }
}
