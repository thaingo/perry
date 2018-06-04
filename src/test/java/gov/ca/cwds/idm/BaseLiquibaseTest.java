package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.BaseLiquibaseTest.CMS_STORE_SCHEMA;
import static gov.ca.cwds.idm.BaseLiquibaseTest.DATABASE_URL;
import static gov.ca.cwds.idm.BaseLiquibaseTest.SCHEMA_EQUALS;
import static gov.ca.cwds.idm.BaseLiquibaseTest.TOKEN_STORE_SCHEMA;

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
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//import static gov.ca.cwds.idm.BaseLiquibaseTest.CMS_STORE_URL;
//import static gov.ca.cwds.idm.BaseLiquibaseTest.TOKEN_STORE_URL;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.identityManager.idmMapping=config/idm.groovy",
//    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
//    "spring.datasource.url=" + CMS_STORE_URL,
    "perry.tokenStore.datasource.url=" + DATABASE_URL + SCHEMA_EQUALS + TOKEN_STORE_SCHEMA,
    "spring.datasource.url=" + DATABASE_URL + SCHEMA_EQUALS + CMS_STORE_SCHEMA
})
public abstract class BaseLiquibaseTest  {

  private static final String H2_DRIVER_CLASS_NAME = "org.h2.Driver";
  private static final String SPRING_BOOT_H2_USER = "sa";
  private static final String SPRING_BOOT_H2_PASSWORD = "";

  public static final String TOKEN_STORE_SCHEMA = "perry";
  public static final String CMS_STORE_SCHEMA = "cwscms";

//  public static final String DATABASE_URL = "jdbc:h2:file:C:/Workspace/perry/testdb;";
//  public static final String TOKEN_STORE_URL = DATABASE_URL + "schema=" + TOKEN_STORE_SCHEMA;
//  public static final String CMS_STORE_URL = DATABASE_URL + "schema=" + CMS_STORE_SCHEMA;
  public static final String DATABASE_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;";

  private static final String TOKEN_STORE_CHANGE_LOG = "liquibase/perry_database_master.xml";
  private static final String CMS_CHANGE_LOG = "liquibase/cwscms_database_base_with_lookups.xml";

  public static final String INIT = "INIT=create schema if not exists ";
  public static final String SET_SCHEMA = ";set schema ";
  public static final String SCHEMA_EQUALS = "schema=";

  @BeforeClass
  public static void beforeClass() throws Exception {
    Class.forName(H2_DRIVER_CLASS_NAME);
    createTokenStoreDatabase();
    createCmsDatabase();
  }

  private static void createDatabase(String schema, String changeLog) throws Exception {
    String url = DATABASE_URL + INIT + schema + "\\" + SET_SCHEMA + schema;
    runLiquibaseScript(url, changeLog);
  }

  private static void createTokenStoreDatabase() throws Exception {
    createDatabase(TOKEN_STORE_SCHEMA, TOKEN_STORE_CHANGE_LOG);
  }

  private static void createCmsDatabase() throws Exception {
    createDatabase(CMS_STORE_SCHEMA, CMS_CHANGE_LOG);
  }

  private static void runLiquibaseScript(String url, String changeLog) throws LiquibaseException {
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
