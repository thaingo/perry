package gov.ca.cwds.idm.service.diff;

public class BooleanDiff extends BaseDiff<Boolean> {

  public BooleanDiff(Boolean oldValue, Boolean newValue) {
    super(oldValue, newValue);
  }

  @Override
  String toStringValue(Boolean value) {
    return String.valueOf(value);
  }
}
