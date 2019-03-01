package gov.ca.cwds.idm.service.validation;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class OptionalPropertyPatternValidator {
  private final Pattern pattern;

  public OptionalPropertyPatternValidator(Pattern pattern) {
    this.pattern = pattern;
  }

  public boolean isValid(String value) {
    if(StringUtils.isBlank(value)){
      return true;
    }
    return pattern.matcher(value).matches();
  }
}
