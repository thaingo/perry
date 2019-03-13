package gov.ca.cwds.config;

import java.util.Locale;

/**
 * Created by dmitry.rudenko on 9/14/2017.
 */
public final class Constants {

  private Constants(){
    //private constructor
  }

  public static final String LOGIN_SERVICE_URL = "/authn/login";
  public static final String VALIDATE_SERVICE_URL = "/authn/validate";
  public static final String TOKEN_SERVICE_URL = "/authn/token";
  public static final String CALLBACK_PARAM = "callback";
  public static final String IDENTITY = "identity";
  public static final String IDENTITY_JSON = "identity_json";
  public static final String ERROR_CONTROLLER = "error";
  public static final Locale DEFAULT_LOCALE = Locale.US;
  public static final int HTTP_STATUS_NOT_HEALTHY = 465;
}

