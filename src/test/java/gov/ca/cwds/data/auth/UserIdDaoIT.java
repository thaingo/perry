package gov.ca.cwds.data.auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gov.ca.cwds.data.persistence.auth.UserId;

import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = {FlywayAutoConfiguration.class,
    LiquibaseAutoConfiguration.class})
@DirtiesContext
@ActiveProfiles("dev")
public class UserIdDaoIT {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserIdDao userIdDao;

  @Test
  public void testFindByLogonId() {
    String logonId = "logonId";
    entityManager.merge(entity("id", logonId));
    List<UserId> users = userIdDao.findActiveByLogonId(logonId);
    assertThat(users.size(), is(1));
  }

  private UserId entity(String id, String logonId) {
    return new UserId(null, null, null, "75D", id, logonId, (short) 5394);
  }
}
