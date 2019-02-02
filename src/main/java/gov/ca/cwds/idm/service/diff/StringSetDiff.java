package gov.ca.cwds.idm.service.diff;

import static java.util.Collections.unmodifiableSet;

import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

public final class StringSetDiff extends BaseDiff<Set<String>> {

  public StringSetDiff(final Set<String> oldValue, final Set<String> newValue) {
    super(oldValue, newValue);
  }

  @Override
  public Set<String> getOldValue() {
    return unmodifiableSet(oldValue);
  }

  @Override
  public Set<String> getNewValue() {
    return unmodifiableSet(newValue);
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
