package gov.ca.cwds;

import gov.ca.cwds.config.TokenServiceConfiguration;
import gov.ca.cwds.security.jwt.JwtService;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManagerFactory;

@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, SessionAutoConfiguration.class})
@ComponentScan("gov.ca.cwds")
@EntityScan("gov.ca.cwds.data.persistence.auth")
@EnableConfigurationProperties({PerryProperties.class})
public class PerryApplication {

  private static final Logger LOG = LoggerFactory.getLogger(PerryApplication.class);
  private static final String LIQUIBASE_PERRY_DATABASE_CREATE_SCHEMA_XML = "liquibase/perry_schema.xml";
  private static final String LIQUIBASE_PERRY_MASTER_XML = "liquibase/perry_database_master.xml";
  private static final String HIBERNATE_DEFAULT_SCHEMA_PROPERTY_NAME = "hibernate.default_schema";
  private static final String HIBERNATE_DDL_AUTO = "hibernate.ddl-auto";

  @Autowired
  private static TokenServiceConfiguration tokenServiceConfiguration;

  @Bean
  public RestTemplate client() {
    return new RestTemplate();
  }

  @Bean
  @Autowired
  public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory);
    return txManager;
  }

  @Bean
  @Autowired
  public JwtService jwtService(PerryProperties perryProperties) {
    return new JwtService(perryProperties.getJwt());
  }

  public static void main(String[] args) {
    upgradePerryDB(tokenServiceConfiguration);
    SpringApplication.run(PerryApplication.class, args);
  }

  private static void upgradePerryDB(TokenServiceConfiguration tokenServiceConfiguration) {
    LOG.info("Upgrading Perry DB...");
    DataSource dataSource = tokenServiceConfiguration.tokenDataSource();
    DatabaseHelper databaseHelper = new DatabaseHelper(dataSource);
    try {
      databaseHelper.runScript(LIQUIBASE_PERRY_DATABASE_CREATE_SCHEMA_XML);
      databaseHelper.runScript(LIQUIBASE_PERRY_MASTER_XML,
          tokenServiceConfiguration.tokenJpaProperties().getProperties().get(HIBERNATE_DEFAULT_SCHEMA_PROPERTY_NAME));
    } catch (Exception e) {
      LOG.error("Upgrading of Perry DB is failed. ", e);
    }

    LOG.info("Finish Upgrading Perry DB");
  }
}
