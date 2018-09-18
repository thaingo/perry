package gov.ca.cwds.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public final class LiquibaseUtils {

  private static final String SPRING_BOOT_H2_USER = "sa";
  private static final String SPRING_BOOT_H2_PASSWORD = "";

  private static final String TOKEN_STORE_CHANGE_LOG = "liquibase/perry_database_master.xml";
  private static final String CMS_CHANGE_LOG = "liquibase/cwscms_database_base_with_lookups.xml";

  public static final String CREATE_SCHEMA = "INIT=create schema if not exists ";
  public static final String SET_SCHEMA = ";set schema ";

  public static final String TOKEN_STORE_SCHEMA = "perry";
  public static final String CMS_STORE_SCHEMA = "cwscms";

  public static final String DATABASE_URL =
      "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;";

  private static final String SCHEMA_EQUALS = "schema=";

  public static final String TOKEN_STORE_URL = DATABASE_URL + SCHEMA_EQUALS + TOKEN_STORE_SCHEMA;
  public static final String CMS_STORE_URL = DATABASE_URL + SCHEMA_EQUALS + CMS_STORE_SCHEMA;

  private LiquibaseUtils() {
  }

  private static void createDatabase(String schema, String changeLog) throws Exception {
    String url = DATABASE_URL + CREATE_SCHEMA + schema + "\\" + SET_SCHEMA + schema;
    runLiquibaseScript(url, changeLog);
  }

  public static void createTokenStoreDatabase() throws Exception {
    createDatabase(TOKEN_STORE_SCHEMA, TOKEN_STORE_CHANGE_LOG);
  }

  public static void createCmsDatabase() throws Exception {
    createDatabase(CMS_STORE_SCHEMA, CMS_CHANGE_LOG);
  }

  public static void runLiquibaseScript(String url, String changeLog) throws LiquibaseException {
    try {
      Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(),
          getDatabase(url, SPRING_BOOT_H2_USER, SPRING_BOOT_H2_PASSWORD));
      liquibase.update((String) null);
    } catch (Exception e) {
      throw new LiquibaseException(e);
    }
  }

  private static Database getDatabase(String url, String user, String password)
      throws SQLException, DatabaseException {
    Connection connection = DriverManager.getConnection(url, user, password);
    return DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(new JdbcConnection(connection));
  }
}
