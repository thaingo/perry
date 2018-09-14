package gov.ca.cwds;

/**
 * Created by Alexander Serbin on 9/11/2018
 */
public final class Constants {

  private Constants() {}

  public static final String H2_DRIVER_CLASS_NAME = "org.h2.Driver";

  public static final String IDM_BASIC_AUTH_USER = "user";
  public static final String IDM_BASIC_AUTH_PASS = "pass";

  private static final String SCHEMA_EQUALS = "schema=";

  public static final String TOKEN_STORE_SCHEMA = "perry";
  public static final String CMS_STORE_SCHEMA = "cwscms";

  public static final String DATABASE_URL =
      "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;";

  public static final String TOKEN_STORE_URL = DATABASE_URL + SCHEMA_EQUALS + TOKEN_STORE_SCHEMA;
  public static final String CMS_STORE_URL = DATABASE_URL + SCHEMA_EQUALS + CMS_STORE_SCHEMA;

  public static final String USERPOOL = "userpool";
  public static final String USER_NO_RACFID_ID = "2be3221f-8c2f-4386-8a95-a68f0282efb0";
  public static final String USER_WITH_RACFID_ID = "24051d54-9321-4dd2-a92f-6425d6c455be";
  public static final String ERROR_USER_ID = "errorUserId";
  public static final String ABSENT_USER_ID = "absentUserId";
  public static final String USER_WITH_RACFID_AND_DB_DATA_ID =
      "d740ec1d-80ae-4d84-a8c4-9bed7a942f5b";
  public static final String USER_WITH_NO_PHONE_EXTENSION = "d740ec1d-66ae-4d84-a8c4-8bed7a942f5b";
  public static final String NEW_USER_SUCCESS_ID = "17067e4e-270f-4623-b86c-b4d4fa527a34";
  public static final String NEW_USER_SUCCESS_ID_2 = "17067e4e-270f-4623-b86c-b4d4fa527a35";
  public static final String NEW_USER_ES_FAIL_ID = "08e14c57-6e5e-48dd-8172-e8949c2a7f76";
  public static final String USER_WITH_INACTIVE_STATUS_COGNITO = "17067e4e-270f-4623-b86c-b4d4fa527a22";
  public static final String ES_ERROR_CREATE_USER_EMAIL = "es.error@create.com";
  public static final String SOME_PAGINATION_TOKEN = "somePaginationToken";
}
