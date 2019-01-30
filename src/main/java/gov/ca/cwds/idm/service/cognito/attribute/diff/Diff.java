package gov.ca.cwds.idm.service.cognito.attribute.diff;

public class Diff<T> {

  private T oldValue;
  private T newValue;

  public Diff(T oldValue, T newValue) {
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
