package gov.ca.cwds.idm.service.mapper;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.data.persistence.auth.StaffAuthorityPrivilege;
import gov.ca.cwds.idm.dto.CwsStaffPrivilege;
import io.dropwizard.jackson.Jackson;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class CwsStaffPrivilegeMapperTest {

  private CwsStaffPrivilegeMapper mapper;
  private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper();

  @Before
  public void before() {
    mapper = new CwsStaffPrivilegeMapper();
  }

  @Test
  public void testToCwsStaffPrivilegeNull() {
    assertThat(mapper.toCwsStaffPrivilege(null), is(empty()));
  }

  @Test
  public void testToCwsStaffPrivilege() throws Exception {
    Set<StaffAuthorityPrivilege> staffAuthorityPrivileges = createStaffAuthorityPrivileges();

    Set<CwsStaffPrivilege> cwsStaffPrivileges =
        mapper.toCwsStaffPrivilege(staffAuthorityPrivileges);

    TypeReference<Set<CwsStaffPrivilege>> typeRef = new TypeReference<Set<CwsStaffPrivilege>>() {};
    final String expected =
        OBJECT_MAPPER.writeValueAsString(
            OBJECT_MAPPER.readValue(
                fixture("fixtures/idm/cws-privileges/cws-privileges-valid.json"), typeRef));

    assertThat(OBJECT_MAPPER.writeValueAsString(cwsStaffPrivileges), is(expected));
  }

  private Set<StaffAuthorityPrivilege> createStaffAuthorityPrivileges() {
    StaffAuthorityPrivilege staffPriv1 =
        new StaffAuthorityPrivilege(
            "15",
            LocalDate.now(),
            null,
            "TEST_USER_ID",
            "AakC6Jm050",
            "P",
            (short) 1482,
            LocalDate.now(),
            LocalTime.now());
    StaffAuthorityPrivilege staffPriv2 =
        new StaffAuthorityPrivilege(
            "15",
            null,
            null,
            "TEST_USER_ID",
            "AakC6Jm051",
            "L",
            (short) 1482,
            LocalDate.now(),
            LocalTime.now());
    StaffAuthorityPrivilege staffPriv3 =
        new StaffAuthorityPrivilege(
            "15",
            null,
            null,
            "TEST_USER_ID",
            "AakC6Jm052",
            "P",
            (short) 1485,
            LocalDate.now(),
            LocalTime.now());
    StaffAuthorityPrivilege staffPriv4 =
        new StaffAuthorityPrivilege(
            "15",
            null,
            null,
            "TEST_USER_ID",
            "AakC6Jm053",
            "P",
            (short) 1482,
            LocalDate.now(),
            LocalTime.now());
    StaffAuthorityPrivilege staffPriv5 =
        new StaffAuthorityPrivilege(
            "15",
            null,
            null,
            "TEST_USER_ID",
            "AakC6Jm054",
            "P",
            (short) 1482,
            LocalDate.now(),
            LocalTime.now());
    StaffAuthorityPrivilege staffPriv6 =
        new StaffAuthorityPrivilege(
            "15",
            null,
            null,
            "TEST_USER_ID",
            "AakC6Jm055",
            "P",
            (short) 6904,
            LocalDate.now(),
            LocalTime.now());
    StaffAuthorityPrivilege staffPriv7 =
        new StaffAuthorityPrivilege(
            "15",
            null,
            null,
            "TEST_USER_ID",
            "AakC6Jm056",
            "P",
            (short) 2182,
            LocalDate.now(),
            LocalTime.now());
    return new HashSet<>(
        Arrays.asList(
            staffPriv1, staffPriv2, staffPriv3, staffPriv4, staffPriv5, staffPriv6, staffPriv7));
  }
}
