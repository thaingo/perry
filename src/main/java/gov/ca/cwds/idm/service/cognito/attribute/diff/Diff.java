package gov.ca.cwds.idm.service.cognito.attribute.diff;

public interface Diff<T> {

  T getNewValue();

  T getOldValue();

  String getNewValueAsString();

  String getOldValueAsString();
}
