package gov.ca.cwds.idm.service.diff;

public class Diff<T> {

  protected final T oldValue;
  protected final T newValue;

  public Diff(final T oldValue, final T newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public T getOldValue() {
    return oldValue;
  }

  public T getNewValue() {
    return newValue;
  }

}
