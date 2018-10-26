package gov.ca.cwds.config;

import java.util.Locale;

/**
 * Created by dmitry.rudenko on 9/14/2017.
 */
public interface Constants {

  String LOGIN_SERVICE_URL = "/authn/login";
  String VALIDATE_SERVICE_URL = "/authn/validate";
  String TOKEN_SERVICE_URL = "/authn/token";
  String CALLBACK_PARAM = "callback";
  String IDENTITY = "identity";
  String IDENTITY_JSON = "identity_json";
  String ERROR_CONTROLLER = "error";
  Locale DEFAULT_LOCALE = Locale.US;
  int HTTP_STATUS_NOT_HEALTHY = 465;
}

