package gov.ca.cwds.service;

import gov.ca.cwds.data.auth.*;
import gov.ca.cwds.data.persistence.auth.*;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffAuthorityPrivilege;
import gov.ca.cwds.data.persistence.auth.StaffUnitAuthority;
import gov.ca.cwds.rest.api.domain.auth.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
  private static final String CWS_OFFICE_ID_1 = "cwsOfficeId1";
  private static final String CWS_OFFICE_ID_2 = "cwsOfficeId2";
  private static final String STAFF_ID_1 = "staffId1";
  private static final String STAFF_ID_2 = "staffId2";
  private static final String STAFF_ID_3 = "staffId3";

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

    userAuthorizationService = new UserAuthorizationService();
    userAuthorizationService.setAssignmentUnitDao(assignmentUnitDao);
    userAuthorizationService.setCwsOfficeDao(cwsOfficeDao);
    userAuthorizationService.setStaffAuthorityPrivilegeDao(staffAuthorityPrivilegeDao);
    userAuthorizationService.setUserIdDao(userIdDao);
    userAuthorizationService.setStaffUnitAuthorityDao(staffUnitAuthorityDao);
    userAuthorizationService.setStaffPersonDao(staffPersonDao);
  }

  @Test
  public void findByRacfIdTest() {


    String racfid = "racfid";
    UserId userId = new UserId(null, null, null, STAFF_ID, ID, LOGON_ID, null);
    Mockito.when(userIdDao.findActiveByLogonIdIn(Collections.singletonList(racfid)))
            .thenReturn(Collections.singletonList(userId));

    Mockito.when(staffAuthorityPrivilegeDao.findSocialWorkerPrivileges(userId.getId()))
            .thenReturn(Collections.emptyList());

    StaffPerson staffPerson = new StaffPerson();
    staffPerson.setId(STAFF_ID);
    staffPerson.setCwsOffice(CWSOFFICE_ID);

    Mockito.when(staffPersonDao.findOne(STAFF_ID)).thenReturn(staffPerson);

    StaffAuthorityPrivilege staffAuthorityPrivilege = new StaffAuthorityPrivilege(
        COUNTY_CODE, null, null, null, null, "L", (short) 1, null, null);

    Mockito.when(staffAuthorityPrivilegeDao.findByUserId(userId.getId()))
        .thenReturn(Collections.singletonList(staffAuthorityPrivilege));

    StaffUnitAuthority staffUnitAuthority = new StaffUnitAuthority(
        "S", null, new Date(), UNIT, STAFF_ID, new Date(), null);

    Mockito.when(staffUnitAuthorityDao.findByStaffPersonId(STAFF_ID))
            .thenReturn(Collections.singletonList(staffUnitAuthority));

    AssignmentUnit assignmentUnit = new AssignmentUnit(
        null, 0, null, COUNTY_CODE, new Date(), CWSOFFICE_ID, null, new Date(), null);

    Mockito.when(assignmentUnitDao.findOne(UNIT))
            .thenReturn(assignmentUnit);

    CwsOffice cwsOffice = new CwsOffice(
        CWSOFFICE_ID, null, null, COUNTY_CWS_CODE , null, null, null, null, null, null, null, null, null, null, null, null, null,
        COUNTY_CODE, null, null, null);

    Mockito.when(cwsOfficeDao.findOne(CWSOFFICE_ID)).thenReturn(cwsOffice);

    UserAuthorization userAuthorization = userAuthorizationService.find(racfid);
    assert userAuthorization.getUserId().equals(LOGON_ID);
    assert userAuthorization.getCwsOffice().equals(cwsOffice);
    assert userAuthorization.getStaffPerson().equals(staffPerson);
    assert !userAuthorization.getSocialWorker();
    assert userAuthorization.getAuthorityPrivilege().size() == 1;
    gov.ca.cwds.rest.api.domain.auth.StaffAuthorityPrivilege authorityPrivilege = userAuthorization.getAuthorityPrivilege().iterator().next();
    assert authorityPrivilege.getAuthPrivilegeCode().equals("L");
    assert authorityPrivilege.getAuthPrivilegeType().equals("1");
    assert authorityPrivilege.getCounty().equals("Humboldt");
    assert authorityPrivilege.getCountyCode().equals(COUNTY_CODE);
    assert authorityPrivilege.getAuthPrivilegeCodeDesc().equals("Staff Person Level of Auth Type");

  }

  @Test
  public void findUsersTest() {

    List<String> keys = Arrays.asList("123", "wqeq", "shuirheuiwyer8876384242342", "5bb");

    String racfid1 = "123";
    UserId userId1 = new UserId(null, null, null, STAFF_ID_1, "id1", racfid1, null);

    String racfid2 = "wqeq";
    UserId userId2 = new UserId(null, null, null, STAFF_ID_2, "id2", racfid2, null);

    String racfid3 = "5bb";
    UserId userId3 = new UserId(null, null, null, STAFF_ID_3, "id3", racfid3, null);

    Mockito.when(userIdDao.findActiveByLogonIdIn(Mockito.anyCollection()))
            .thenReturn(Arrays.asList(userId1, userId2, userId3));

    CwsOffice cwsOffice1 =
        new CwsOffice(
            CWS_OFFICE_ID_1,
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

    CwsOffice cwsOffice2 =
        new CwsOffice(
            CWS_OFFICE_ID_2,
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


    Mockito.when(cwsOfficeDao.findByOfficeIdIn(Mockito.anyCollection())).thenReturn(Arrays.asList(cwsOffice1, cwsOffice2));

    StaffPerson staffPerson1 = new StaffPerson();
    staffPerson1.setId(STAFF_ID_1);
    staffPerson1.setCwsOffice(CWS_OFFICE_ID_1);

    StaffPerson staffPerson2 = new StaffPerson();
    staffPerson2.setId(STAFF_ID_2);
    staffPerson2.setCwsOffice(CWS_OFFICE_ID_2);

    StaffPerson staffPerson3 = new StaffPerson();
    staffPerson3.setId(STAFF_ID_3);

    Mockito.when(staffPersonDao.findByIdIn(Mockito.anyCollection())).thenReturn(Arrays.asList(staffPerson1, staffPerson2, staffPerson3));

    List<UserAuthorization> result = userAuthorizationService.findUsers(keys);

    assert result.size() == 3;
    assert result.get(0).getStaffPerson() != null;

   if (result.get(1).getCwsOffice() != null) {
     assert result.get(1).getCwsOffice().getCountySpecificCode().equals(COUNTY_CODE);
   }
   else {
     assert result.get(1).getStaffPerson().getCwsOffice() == null;
   }

    if (result.get(2).getCwsOffice() != null) {
      assert result.get(2).getCwsOffice().getCountySpecificCode().equals("COUNTY_CODE");
    }
    else {
      assert result.get(2).getStaffPerson().getCwsOffice() == null;
    }


  }

  @Test
  public void testLongIds() {
    List<String> keys = Arrays.asList("shuirheuiwyer887638424232314", "shuirheuiwyer8876384242345", "shuirheuiwyer8876384242342", "shuirheuiwyer887638424jekw");
    List<UserAuthorization> result = userAuthorizationService.findUsers(keys);

    assert result.size() == 0;

  }
}
