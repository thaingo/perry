package gov.ca.cwds.util;

import gov.ca.cwds.idm.dto.User;

public final class UserNameFormatter {

  private UserNameFormatter() {}

  public static String formatUserFullName(User user) {
    return formatUserFullName(user.getLastName(), user.getFirstName());
  }

  public static String formatUserFullName(String lastName, String firstName) {
    return lastName + ", " + firstName;
  }
}
