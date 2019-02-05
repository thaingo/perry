package gov.ca.cwds.idm.service.diff;

public abstract class BaseDiff<T> implements Diff<T> {

  protected final T oldValue;
  protected final T newValue;

  public BaseDiff(final T oldValue, final T newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  public T getOldValue() {
    return oldValue;
  }

  @Override
  public T getNewValue() {
    return newValue;
  }
}
