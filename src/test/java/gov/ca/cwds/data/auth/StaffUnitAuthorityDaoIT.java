package gov.ca.cwds.data.auth;

import gov.ca.cwds.data.persistence.auth.StaffUnitAuthority;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = {FlywayAutoConfiguration.class})
@DirtiesContext
@ActiveProfiles("dev")
public class StaffUnitAuthorityDaoIT {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private StaffUnitAuthorityDao staffUnitAuthorityDao;

  @Test
  public void testFindByStaffPersonId() {
    String userId = "userId";
    entityManager.merge(entity("id", userId, null));
    entityManager.merge(entity("id1", userId, new Date()));
    entityManager.merge(entity("id2", userId, new Date()));
    List<StaffUnitAuthority> authorities = staffUnitAuthorityDao.findByStaffPersonId(userId);
    assertThat(authorities.size(), is(1));

  }

  private StaffUnitAuthority entity(String id, String userId, Date endDate) {
    return new StaffUnitAuthority("A", "19", endDate, "NZGDRrd00E", userId, new Date(), id);
  }

}
