package gov.ca.cwds.idm.service.diff;

public abstract class BaseDiff<T> implements Diff<T> {

  private T oldValue;
  private T newValue;

  public BaseDiff(T oldValue, T newValue) {
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

  @Override
  public String getOldValueAsString() {
    return toStringValue(oldValue);
  }

  @Override
  public String getNewValueAsString() {
    return toStringValue(newValue);
  }

  abstract String toStringValue(T value);
}
