package gov.ca.cwds.security.utils;

/**
 * Created by dmitry.rudenko on 9/29/2017.
 */
public class Utils {

  private UtilsClass() {
      throw new IllegalStateException("Utility class");
  }

  public static String replaceCRLF(String string) {
    return string.replaceAll("[\\n\\r]", " ");
  }
}
