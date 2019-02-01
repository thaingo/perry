package gov.ca.cwds.idm.service.diff;

import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

public class StringSetDiff extends BaseDiff<Set<String>> {

  public StringSetDiff(Set<String> oldValue, Set<String> newValue) {
    super(oldValue, newValue);
  }

  @Override
  String toStringValue(Set<String> value) {
    if (value == null) {
      return "";
    } else {
      return StringUtils.join(new TreeSet<>(value), ", ");
    }
  }
}
