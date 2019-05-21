package gov.ca.cwds.idm.service.rule;

import gov.ca.cwds.idm.exception.IdmException;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ErrorRule {

  private BooleanSupplier condition;
  private Supplier<IdmException> exceptionSupplier;

  public ErrorRule(BooleanSupplier condition, Supplier<IdmException> exceptionSupplier) {
    this.condition = condition;
    this.exceptionSupplier = exceptionSupplier;
  }

  public final boolean hasError() {
    return condition.getAsBoolean();
  }

  public final IdmException createException(){
    return exceptionSupplier.get();
  }

  public final void check(){
    if(hasError()) {
      throw createException();
    }
  }
}
