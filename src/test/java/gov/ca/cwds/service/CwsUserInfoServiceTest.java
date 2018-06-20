package gov.ca.cwds.service;

import gov.ca.cwds.data.auth.AssignmentUnitDao;
import gov.ca.cwds.data.auth.CwsOfficeDao;
import gov.ca.cwds.data.auth.StaffAuthorityPrivilegeDao;
import gov.ca.cwds.data.auth.StaffPersonDao;
import gov.ca.cwds.data.auth.StaffUnitAuthorityDao;
import gov.ca.cwds.data.auth.UserIdDao;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.data.persistence.auth.UserId;
import gov.ca.cwds.service.dto.CwsUserInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class CwsUserInfoServiceTest {

  private static final String COUNTY_CODE = "12";
  private static final short COUNTY_CWS_CODE = 1079;
  private static final String CWS_OFFICE_ID_1 = "cwsOfficeId1";
  private static final String CWS_OFFICE_ID_2 = "cwsOfficeId2";
  private static final String STAFF_ID_1 = "staffId1";
  private static final String STAFF_ID_2 = "staffId2";
  private static final String STAFF_ID_3 = "staffId3";

  private UserIdDao userIdDao;
  private CwsOfficeDao cwsOfficeDao;
  private StaffPersonDao staffPersonDao;
  private CwsUserInfoService cwsUserInfoService;

  @Before
  public void init() {
    userIdDao = Mockito.mock(UserIdDao.class);
    cwsOfficeDao = Mockito.mock(CwsOfficeDao.class);
    staffPersonDao = Mockito.mock(StaffPersonDao.class);

    cwsUserInfoService = new CwsUserInfoService();
    cwsUserInfoService.setCwsOfficeDao(cwsOfficeDao);
    cwsUserInfoService.setUserIdDao(userIdDao);
    cwsUserInfoService.setStaffPersonDao(staffPersonDao);
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

    Mockito.when(cwsOfficeDao.findByOfficeIdIn(Mockito.anyCollection()))
        .thenReturn(Arrays.asList(cwsOffice1, cwsOffice2));

    StaffPerson staffPerson1 = new StaffPerson();
    staffPerson1.setId(STAFF_ID_1);
    staffPerson1.setCwsOffice(CWS_OFFICE_ID_1);

    StaffPerson staffPerson2 = new StaffPerson();
    staffPerson2.setId(STAFF_ID_2);
    staffPerson2.setCwsOffice(CWS_OFFICE_ID_2);

    StaffPerson staffPerson3 = new StaffPerson();
    staffPerson3.setId(STAFF_ID_3);

    Mockito.when(staffPersonDao.findByIdIn(Mockito.anyCollection()))
        .thenReturn(Arrays.asList(staffPerson1, staffPerson2, staffPerson3));

    List<CwsUserInfo> result = cwsUserInfoService.findUsers(keys);

    assert result.size() == 3;
    assert result.get(0).getStaffPerson() != null;

    if (result.get(1).getCwsOffice() != null) {
      assert result.get(1).getCwsOffice().getCountySpecificCode().equals(COUNTY_CODE);
    } else {
      assert result.get(1).getStaffPerson().getCwsOffice() == null;
    }

    if (result.get(2).getCwsOffice() != null) {
      assert result.get(2).getCwsOffice().getCountySpecificCode().equals("COUNTY_CODE");
    } else {
      assert result.get(2).getStaffPerson().getCwsOffice() == null;
    }
  }

  @Test
  public void testLongIds() {
    List<String> keys =
        Arrays.asList(
            "shuirheuiwyer887638424232314",
            "shuirheuiwyer8876384242345",
            "shuirheuiwyer8876384242342",
            "shuirheuiwyer887638424jekw");
    List<CwsUserInfo> result = cwsUserInfoService.findUsers(keys);

    assert result.size() == 0;
  }
}
