package gov.ca.cwds.service;

import gov.ca.cwds.data.auth.*;
import gov.ca.cwds.data.persistence.auth.*;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Date;

/**
 * Created by dmitry.rudenko on 10/3/2017.
 */
public class UserAuthorizationServiceTest {

  public static final String STAFF_ID = "staffId";
  public static final String ID = "id";
  public static final String LOGONID = "logonid";
  public static final String UNIT = "unit";
  public static final String CWSOFFICE = "cwsoffice";
  public static final String COUNTY_CODE = "12";

  @Test
  public void test() {
    UserIdDao userIdDao = Mockito.mock(UserIdDao.class);
    StaffAuthorityPrivilegeDao staffAuthorityPrivilegeDao = Mockito.mock(StaffAuthorityPrivilegeDao.class);
    StaffUnitAuthorityDao staffUnitAuthorityDao = Mockito.mock(StaffUnitAuthorityDao.class);
    CwsOfficeDao cwsOfficeDao = Mockito.mock(CwsOfficeDao.class);
    AssignmentUnitDao assignmentUnitDao = Mockito.mock(AssignmentUnitDao.class);
    StaffPersonDao staffPersonDao = Mockito.mock(StaffPersonDao.class);

    UserAuthorizationService userAuthorizationService = new UserAuthorizationService();
    userAuthorizationService.setAssignmentUnitDao(assignmentUnitDao);
    userAuthorizationService.setCwsOfficeDao(cwsOfficeDao);
    userAuthorizationService.setStaffAuthorityPrivilegeDao(staffAuthorityPrivilegeDao);
    userAuthorizationService.setUserIdDao(userIdDao);
    userAuthorizationService.setStaffUnitAuthorityDao(staffUnitAuthorityDao);
    userAuthorizationService.setStaffPersonDao(staffPersonDao);

    String racfid = "racfid";

    UserId userId = Mockito.mock(UserId.class);
    Mockito.when(userId.getId()).thenReturn(ID);
    Mockito.when(userId.getLogonId()).thenReturn(LOGONID);
    Mockito.when(userId.getStaffPersonId()).thenReturn(STAFF_ID);

    Mockito.when(userIdDao.findActiveByLogonId(racfid))
            .thenReturn(Collections.singletonList(userId));

    Mockito.when(staffAuthorityPrivilegeDao.findSocialWorkerPrivileges(userId.getId()))
            .thenReturn(Collections.emptyList());

    StaffPerson staffPerson = Mockito.mock(StaffPerson.class);
    Mockito.when(staffPerson.getCountyCode()).thenReturn(COUNTY_CODE);
    Mockito.when(staffPerson.getId()).thenReturn(STAFF_ID);
    Mockito.when(staffPersonDao.findOne(STAFF_ID)).thenReturn(staffPerson);

    StaffAuthorityPrivilege staffAuthorityPrivilege = Mockito.mock(StaffAuthorityPrivilege.class);
    Mockito.when(staffAuthorityPrivilege.getCountySpecificCode()).thenReturn(COUNTY_CODE);
    Mockito.when(staffAuthorityPrivilege.getLevelOfAuthPrivilegeCode()).thenReturn("L");
    Mockito.when(staffAuthorityPrivilege.getLevelOfAuthPrivilegeType()).thenReturn((short) 1);


    StaffUnitAuthority staffUnitAuthority = Mockito.mock(StaffUnitAuthority.class);
    Mockito.when(staffUnitAuthority.getFkasgUnit()).thenReturn(UNIT);
    Mockito.when(staffUnitAuthority.getEndDate()).thenReturn(new Date());
    Mockito.when(staffUnitAuthority.getStartDate()).thenReturn(new Date());
    Mockito.when(staffUnitAuthority.getStaffPersonId()).thenReturn(STAFF_ID);
    Mockito.when(staffUnitAuthority.getAuthorityCode()).thenReturn("S");

    Mockito.when(staffUnitAuthorityDao.findByStaffPersonId(STAFF_ID))
            .thenReturn(Collections.singletonList(staffUnitAuthority));

    Mockito.when(staffAuthorityPrivilegeDao.findByUserId(userId.getId()))
            .thenReturn(Collections.singletonList(staffAuthorityPrivilege));


    AssignmentUnit assignmentUnit = Mockito.mock(AssignmentUnit.class);
    Mockito.when(assignmentUnit.getCwsOfficeId()).thenReturn(CWSOFFICE);
    Mockito.when(assignmentUnit.getEndDate()).thenReturn(new Date());
    Mockito.when(assignmentUnit.getStartDate()).thenReturn(new Date());
    Mockito.when(assignmentUnit.getCountySpecificCode()).thenReturn(COUNTY_CODE);

    Mockito.when(assignmentUnitDao.findOne(UNIT))
            .thenReturn(assignmentUnit);

    CwsOffice cwsOffice = Mockito.mock(CwsOffice.class);
    Mockito.when(cwsOffice.getCountySpecificCode()).thenReturn(COUNTY_CODE);
    Mockito.when(cwsOfficeDao.findByStaffPersonId(STAFF_ID)).thenReturn(Collections.singletonList(cwsOffice));

    UserAuthorization userAuthorization = userAuthorizationService.find(racfid);
    assert userAuthorization.getUserId().equals(LOGONID);
    assert !userAuthorization.getSocialWorker();
    assert userAuthorization.getStaffPerson() != null;
    assert userAuthorization.getStaffPerson().getId().equals(STAFF_ID);
    assert userAuthorization.getAuthorityPrivilege().size() == 1;
    gov.ca.cwds.rest.api.domain.auth.StaffAuthorityPrivilege authorityPrivilege = userAuthorization.getAuthorityPrivilege().iterator().next();
    assert authorityPrivilege.getAuthPrivilegeCode().equals("L");
    assert authorityPrivilege.getAuthPrivilegeType().equals("1");
    assert authorityPrivilege.getCounty().equals("Humboldt");
    assert authorityPrivilege.getCountyCode().equals(COUNTY_CODE);
    assert authorityPrivilege.getAuthPrivilegeCodeDesc().equals("Staff Person Level of Auth Type");

  }
}
