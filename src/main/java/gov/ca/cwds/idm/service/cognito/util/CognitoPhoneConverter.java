package gov.ca.cwds.idm.service.cognito.util;

import org.apache.commons.lang3.StringUtils;

public class CognitoPhoneConverter {

  public static final String PLUS = "+";

  private CognitoPhoneConverter(){}

  public static String toCognitoFormat(String input) {
    if (input == null) {
      return null;
    } else if (StringUtils.isBlank(input)) {
      return "";
    } else if (input.startsWith(PLUS)) {
      return input;
    } else {
      return PLUS + input;
    }
  }

  public static String fromCognitoFormat(String input) {
    if (input == null) {
      return null;
    } else if (StringUtils.isBlank(input)) {
      return "";
    } else if (input.startsWith(PLUS)) {
      return input.substring(1);
    } else {
      return input;
    }
  }
}
