package gov.ca.cwds.idm.service.validation;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class PatternValidator {
  private final Pattern pattern;

  public PatternValidator(String patternStr) {
    this.pattern = Pattern.compile(patternStr);
  }

  public boolean isValid(String value) {
    if(StringUtils.isBlank(value)){
      throw new IllegalArgumentException("Input value should be non-blank");
    }
    return pattern.matcher(value).matches();
  }
}
