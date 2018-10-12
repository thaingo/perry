package gov.ca.cwds.idm.service.filter;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
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

  public void testNoRoles() {
    assertThat(filter(toSet()), empty());
  }

  public void testOnlyNonStandardRoles() {
    assertThat(filter(toSet("manager", "developer")), empty());
  }

  public void testStateAdmin() {
    assertThat(filter(
        toSet(STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN, CALS_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER,
            "developer")), is(toSet(STATE_ADMIN)));
  }

  public void testCountyAdmin() {
    assertThat(filter(
        toSet(COUNTY_ADMIN, OFFICE_ADMIN, CALS_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER,
            "developer")), is(toSet(COUNTY_ADMIN)));
  }

  public void testOfficeAdmin() {
    assertThat(filter(
        toSet(OFFICE_ADMIN, CALS_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER, "developer")),
        is(toSet(OFFICE_ADMIN)));
  }

  public void testCalsAdmin() {
    assertThat(filter(
        toSet(CALS_ADMIN, CWS_WORKER, CALS_EXTERNAL_WORKER, "developer")),
        is(toSet(CALS_ADMIN)));
  }

  public void testCwsWorker() {
    assertThat(filter(
        toSet(CWS_WORKER, CALS_EXTERNAL_WORKER, "developer")), is(toSet(CWS_WORKER)));
  }

  public void testCalsWorker() {
    assertThat(filter(
        toSet(CALS_EXTERNAL_WORKER, "developer")), is(toSet(CALS_EXTERNAL_WORKER)));
  }
}
