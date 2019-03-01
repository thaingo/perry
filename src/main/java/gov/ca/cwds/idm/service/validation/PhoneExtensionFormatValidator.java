package gov.ca.cwds.idm.service.validation;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class PhoneExtensionFormatValidator {

  private static final String PHONE_EXTENSION_PATTERN_STRING = "\\d{0,7}";
  private static final Pattern PHONE_EXTENSION_PATTERN = Pattern.compile(PHONE_EXTENSION_PATTERN_STRING);

  private PhoneExtensionFormatValidator(){}

  public static boolean isValid(String phoneNumber) {
    if(StringUtils.isBlank(phoneNumber)){
      return true;
    }
    return PHONE_EXTENSION_PATTERN.matcher(phoneNumber).matches();
  }
}
