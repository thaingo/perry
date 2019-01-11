package gov.ca.cwds.idm.service.validation;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class PhoneNumberValidator {

  private static final  String PHONE_PATTERN_STRING = "\\d+";
  private static final  Pattern PHONE_PATTERN = Pattern.compile(PHONE_PATTERN_STRING);

  private PhoneNumberValidator(){}

  public static boolean isValid(String phoneNumber) {
    if(StringUtils.isBlank(phoneNumber)){
      return false;
    }
    return PHONE_PATTERN.matcher(phoneNumber).matches();
  }
}
