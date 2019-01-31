package gov.ca.cwds.idm.service.cognito.attribute.diff;

public class StringDiff extends BaseDiff<String> {

  public StringDiff(String oldValue, String newValue) {
    super(oldValue, newValue);
  }

  @Override
  String toStringValue(String val) {
    return val;
  }
}
