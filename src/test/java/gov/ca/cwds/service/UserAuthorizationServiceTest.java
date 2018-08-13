package gov.ca.cwds.service;

import gov.ca.cwds.data.auth.AssignmentUnitDao;
import gov.ca.cwds.data.auth.CwsOfficeDao;
import gov.ca.cwds.data.auth.StaffAuthorityPrivilegeDao;
import gov.ca.cwds.data.auth.StaffPersonDao;
import gov.ca.cwds.data.auth.StaffUnitAuthorityDao;
import gov.ca.cwds.data.auth.UserIdDao;
import gov.ca.cwds.data.persistence.auth.AssignmentUnit;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffAuthorityPrivilege;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.data.persistence.auth.StaffUnitAuthority;
import gov.ca.cwds.data.persistence.auth.UserId;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import java.util.Collections;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by dmitry.rudenko on 10/3/2017.
 */
public class UserAuthorizationServiceTest {

  private static final String STAFF_ID = "staffId";
  private static final String ID = "id";
  private static final String LOGON_ID = "logonId";
  private static final String UNIT = "unit";
  private static final String CWSOFFICE_ID = "cwsofficeId";
  private static final String COUNTY_CODE = "12";
  private static final short COUNTY_CWS_CODE = 1079;

  private UserIdDao userIdDao;
  private StaffAuthorityPrivilegeDao staffAuthorityPrivilegeDao;
  private StaffUnitAuthorityDao staffUnitAuthorityDao;
  private CwsOfficeDao cwsOfficeDao;
  private AssignmentUnitDao assignmentUnitDao;
  private StaffPersonDao staffPersonDao;
  private UserAuthorizationService userAuthorizationService;

  @Before
  public void init() {
    userIdDao = Mockito.mock(UserIdDao.class);
    staffAuthorityPrivilegeDao = Mockito.mock(StaffAuthorityPrivilegeDao.class);
    staffUnitAuthorityDao = Mockito.mock(StaffUnitAuthorityDao.class);
    cwsOfficeDao = Mockito.mock(CwsOfficeDao.class);
    assignmentUnitDao = Mockito.mock(AssignmentUnitDao.class);
    staffPersonDao = Mockito.mock(StaffPersonDao.class);

    CwsUserInfoService cwsUserInfoService = new CwsUserInfoService();
    cwsUserInfoService.setCwsOfficeDao(cwsOfficeDao);
    cwsUserInfoService.setUserIdDao(userIdDao);
    cwsUserInfoService.setStaffPersonDao(staffPersonDao);

    userAuthorizationService = new UserAuthorizationService();
    userAuthorizationService.setCwsUserInfoService(cwsUserInfoService);
  }

  @Test
  public void findByRacfIdTest() {

    String racfid = "racfid";
    UserId userId = new UserId(null, null, null, STAFF_ID, ID, LOGON_ID, null);
    Mockito.when(userIdDao.findActiveByLogonId(racfid))
        .thenReturn(Collections.singleton(userId));

    Mockito.when(staffAuthorityPrivilegeDao.findSocialWorkerPrivileges(userId.getId()))
        .thenReturn(Collections.emptyList());

    StaffPerson staffPerson = new StaffPerson();
    staffPerson.setId(STAFF_ID);
    staffPerson.setCwsOffice(CWSOFFICE_ID);

    Mockito.when(staffPersonDao.findOne(STAFF_ID)).thenReturn(staffPerson);

    StaffAuthorityPrivilege staffAuthorityPrivilege =
        new StaffAuthorityPrivilege(
            COUNTY_CODE, null, null, null, null, "L", (short) 1, null, null);

    Mockito.when(staffAuthorityPrivilegeDao.findByUserId(userId.getId()))
        .thenReturn(Collections.singletonList(staffAuthorityPrivilege));

    StaffUnitAuthority staffUnitAuthority =
        new StaffUnitAuthority("S", null, new Date(), UNIT, STAFF_ID, new Date(), null);

    Mockito.when(staffUnitAuthorityDao.findByStaffPersonId(STAFF_ID))
        .thenReturn(Collections.singletonList(staffUnitAuthority));

    AssignmentUnit assignmentUnit =
        new AssignmentUnit(
            null, 0, null, COUNTY_CODE, new Date(), CWSOFFICE_ID, null, new Date(), null);

    Mockito.when(assignmentUnitDao.findOne(UNIT)).thenReturn(assignmentUnit);

    CwsOffice cwsOffice =
        new CwsOffice(
            CWSOFFICE_ID,
            null,
            null,
            COUNTY_CWS_CODE,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            COUNTY_CODE,
            null,
            null,
            null);

    Mockito.when(cwsOfficeDao.findOne(CWSOFFICE_ID)).thenReturn(cwsOffice);

    UserAuthorization userAuthorization = userAuthorizationService.find(racfid);
    assert userAuthorization.getUserId().equals(LOGON_ID);
    assert userAuthorization.getCwsOffice().equals(cwsOffice);
    assert userAuthorization.getStaffPerson().equals(staffPerson);
    assert !userAuthorization.getSocialWorker();
    assert userAuthorization.getAuthorityPrivilege().size() == 1;
    gov.ca.cwds.rest.api.domain.auth.StaffAuthorityPrivilege authorityPrivilege =
        userAuthorization.getAuthorityPrivilege().iterator().next();
    assert authorityPrivilege.getAuthPrivilegeCode().equals("L");
    assert authorityPrivilege.getAuthPrivilegeType().equals("1");
    assert authorityPrivilege.getCounty().equals("Humboldt");
    assert authorityPrivilege.getCountyCode().equals(COUNTY_CODE);
    assert authorityPrivilege.getAuthPrivilegeCodeDesc().equals("Staff Person Level of Auth Type");
  }
}
