package gov.ca.cwds.service.scripts;

import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;

import gov.ca.cwds.UniversalUserToken;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/**
 * Created by leonid.marushevskyi on 1/3/2018.
 */
public class DefaultMappingTest extends ScriptTestBase {

  private List<String> roles;

  protected UniversalUserToken createUniversalUserToken() {
    UniversalUserToken result = new UniversalUserToken();
    result.setUserId("userId");
    if (roles != null) {
      result.getRoles().addAll(roles);
    }
    result.setParameter("userName", "testUserName");
    result.setParameter("custom:office", "15");
    result.getPermissions().addAll(Arrays.asList("permission1", "permission2"));
    return result;
  }

  @Test
  public void testGroovyMapping() throws Exception {
    roles = Arrays.asList("role1", "role2");
    test("/scripts/default/default.groovy", "/scripts/default/default.json", "scripts/default/authz.json");
  }

  @Test
  public void testGroovyMappingNonRacf() throws Exception {
    roles = Arrays.asList("role1", "role2");
    test("/scripts/default/default.groovy", "/scripts/default/default-nonracf.json", null);
  }

  @Test
  public void testGroovyMappingCountyAdmin() throws Exception {
    roles = Arrays.asList("role1", "role2", COUNTY_ADMIN);
    test("/scripts/default/default.groovy", "/scripts/default/default-admin.json", "scripts/default/authz.json");
  }

  @Test
  public void testGroovyMappingNonRacfIdCountyAdmin() throws Exception {
    roles = Arrays.asList("role1", "role2", COUNTY_ADMIN);
    test("/scripts/default/default.groovy", "/scripts/default/default-nonracf-admin.json", null);
  }

  @Test
  public void testGroovyMappingOfficeAdmin() throws Exception {
    roles = Arrays.asList("role1", "role2", OFFICE_ADMIN);
    test("/scripts/default/default.groovy", "/scripts/default/default-office-admin.json", "scripts/default/authz.json");
  }

  @Test
  public void testGroovyMappingNonRacfIdOfficeAdmin() throws Exception {
    roles = Arrays.asList("role1", "role2", OFFICE_ADMIN);
    test("/scripts/default/default.groovy", "/scripts/default/default-nonracf-office-admin.json", null);
  }

  @Test
  public void testGroovyMappingCaseCarryingStaffPerson() throws Exception {
    roles = Arrays.asList("role1", "role2");
    test("/scripts/default/default.groovy", "/scripts/default/default-case-carrying-staff-person.json", "scripts/default/auth-case-carrying.json");
  }

  @Test
  public void nonCaseCarryingWorkerMapping_success_whenNoPrivilegeButHasAuthorityAndNoAssignment() throws Exception {
    test("/scripts/default/default.groovy", "/scripts/default/non-case-carrying/default-no-privilege-and-authority.json", "scripts/default/non-case-carrying/auth-no-privilege-and-authority.json");
  }

  @Test
  public void nonCaseCarryingWorkerMapping_success_whenPrivilegeAndAuthorityAndNoAssignment() throws Exception {
    test("/scripts/default/default.groovy", "/scripts/default/non-case-carrying/default-privilege-and-authority.json", "scripts/default/non-case-carrying/auth-privilege-and-authority.json");
  }

  @Test
  public void nonCaseCarryingWorkerMapping_success_whenPrivilegeAndNoAuthorityAndNoAssignment() throws Exception {
    test("/scripts/default/default.groovy", "/scripts/default/non-case-carrying/default-privilege-no-authority.json", "scripts/default/non-case-carrying/auth-privilege-no-authority.json");
  }

  @Test
  public void nonCaseCarryingWorkerMapping_fail_whenPrivilegeAndAuthorityAndHasAssignment() throws Exception {
    test("/scripts/default/default.groovy", "/scripts/default/non-case-carrying/default-has-assignment-privilege-and-authority.json", "scripts/default/non-case-carrying/auth-has-assignment-privilege-and-authority.json");
  }
}
