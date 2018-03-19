package gov.ca.cwds.data.auth;

import gov.ca.cwds.data.persistence.auth.AssignmentUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = {FlywayAutoConfiguration.class})
@DirtiesContext
@ActiveProfiles("dev")
public class AssignmentUnitDaoIT {
  private Date startDate = new Date();

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AssignmentUnitDao assignmentUnitDao;

  @Test
  public void testFind() {
    String id = "testFind";
    AssignmentUnit assignmentUnit = entity(id);
    entityManager.merge(assignmentUnit);
    AssignmentUnit found = assignmentUnitDao.findOne(id);
    assertThat(found, is(assignmentUnit));
  }

  private AssignmentUnit entity(String id) {
    return new AssignmentUnit(1234567L, 123, "A", "19", null, "NZGDRrd00E", "75D",
            startDate, id);
  }
}
