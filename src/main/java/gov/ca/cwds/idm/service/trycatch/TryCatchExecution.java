package gov.ca.cwds.idm.service.trycatch;

import gov.ca.cwds.idm.service.OperationResultType;

public abstract class TryCatchExecution<T> {

  private OperationResultType resultType;
  private Exception exception;

  @SuppressWarnings({"fb-contrib:PCOA_PARTIALLY_CONSTRUCTED_OBJECT_ACCESS"})
  //implementations of tryMethod() should not use resultType field by design
  public TryCatchExecution(T input){
    try {
      tryMethod(input);
      resultType = OperationResultType.SUCCESS;

    } catch (Exception e) {
      resultType = OperationResultType.FAIL;
      exception = e;
      catchMethod(e);
    }
  }

  protected abstract void tryMethod(T input);

  protected abstract void catchMethod(Exception e);

  public OperationResultType getResultType() {
    return resultType;
  }

  public Exception getException() {
    return exception;
  }
}
