package gov.ca.cwds.idm.service.diff;

public interface Diff<T> {

  T getNewValue();

  T getOldValue();
}
