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

}
