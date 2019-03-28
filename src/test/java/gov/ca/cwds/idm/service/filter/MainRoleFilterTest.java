package gov.ca.cwds.idm.service.filter;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.service.filter.MainRoleFilter.filter;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MainRoleFilterTest {

  @Test
  public void testNull() {
    assertThat(filter(null), empty());
  }

  @Test
  public void testNoRoles() {
    assertThat(filter(toSet()), empty());
  }

  @Test
  public void testOnlyNonStandardRoles() {
    assertThat(filter(toSet("manager", "developer")), empty());
  }

  @Test
  public void testStateAdmin() {
    assertThat(filter(
        toSet(STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER,
            "developer")), is(toSet(STATE_ADMIN)));
  }

  public void testCountyAdmin() {
    assertThat(filter(
        toSet(COUNTY_ADMIN, OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER,
            "developer")), is(toSet(COUNTY_ADMIN)));
  }

  @Test
  public void testOfficeAdmin() {
    assertThat(filter(
        toSet(OFFICE_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER, "developer")),
        is(toSet(OFFICE_ADMIN)));
  }

  @Test
  public void testCwsWorker() {
    assertThat(filter(
        toSet(CWS_WORKER, CALS_EXTERNAL_WORKER, "developer")), is(toSet(CWS_WORKER)));
  }

  @Test
  public void testCalsWorker() {
    assertThat(filter(
        toSet(CALS_EXTERNAL_WORKER, "developer")), is(toSet(CALS_EXTERNAL_WORKER)));
  }

  @Test
  public void testSuperAdmin() {
    assertThat(filter(
        toSet(SUPER_ADMIN, STATE_ADMIN)), is(toSet(SUPER_ADMIN)));
  }
}
