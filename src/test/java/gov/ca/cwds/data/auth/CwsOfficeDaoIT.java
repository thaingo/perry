package gov.ca.cwds.data.auth;

import gov.ca.cwds.data.persistence.auth.CwsOffice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = {FlywayAutoConfiguration.class})
@DirtiesContext
@ActiveProfiles("dev")
public class CwsOfficeDaoIT {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private CwsOfficeDao cwsOfficeDao;

  @Test
  public void findByStaffPersonId() {
    entityManager.merge(getCwsOffice("1", "75B"));
    entityManager.merge(getCwsOffice("2", "75B"));
    entityManager.merge(getCwsOffice("3", "75C"));
    List<CwsOffice> result = cwsOfficeDao.findByStaffPersonId("75B");
    assertThat(result.size(), is(2));
    assertThat("only 1st and 2nd offices should be loaded", result.stream().
            map(CwsOffice::getOfficeId).
            collect(Collectors.toSet()).
            containsAll(Arrays.asList("1", "2")));

  }

  private CwsOffice getCwsOffice(String officeId, String staffPersonId) {
    return new CwsOffice(officeId,
            0L,
            "19",
            (short) 1088,
            "N",
            "N",
            " ",
            0L,
            0,
            " ",
            0L,
            0,
            staffPersonId,
            " ",
            "  ",
            " ",
            " ",
            "12 ",
            (short) 0,
            (short) 0,
            " ");
  }
}
