package gov.ca.cwds.idm;

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
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=none")
public abstract class BaseTokenStoreLiquibaseTest implements ApplicationContextAware {

  private static final String H2_DRIVER_CLASS_NAME = "org.h2.Driver";
  private static final String SPRING_BOOT_H2_USER = "sa";
  private static final String SPRING_BOOT_H2_PASSWORD = "";
  private static final String CHANGE_LOG = "liquibase/perry_database_master.xml";

  //usually "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
  private String springBootH2url;

  @Before
  public void beforeSuper() throws Exception {
    createDatabase(springBootH2url);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    DataSourceProperties datasourceProperties =
        applicationContext.getBean("tokenStoreDatasourceProperties", DataSourceProperties.class);
    springBootH2url = datasourceProperties.determineUrl();
  }

  private static void createDatabase(String url) throws Exception {
    Class.forName(H2_DRIVER_CLASS_NAME);
    runLiquibaseScript(url);
  }

  private static void runLiquibaseScript(String url) throws LiquibaseException {
    try {
      Liquibase liquibase = new Liquibase(CHANGE_LOG, new ClassLoaderResourceAccessor(),
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
