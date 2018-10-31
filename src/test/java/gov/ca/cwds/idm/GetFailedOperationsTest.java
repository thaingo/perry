package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.IdmResource.DATETIME_FORMAT_PATTERN;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_NO_RACFID_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_AND_DB_DATA_ID;
import static gov.ca.cwds.idm.TestCognitoServiceFacade.USER_WITH_RACFID_ID;
import static gov.ca.cwds.idm.persistence.ns.OperationType.CREATE;
import static gov.ca.cwds.idm.persistence.ns.OperationType.UPDATE;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertExtensible;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.Iterables;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.idm.persistence.ns.entity.UserLog;
import gov.ca.cwds.idm.util.WithMockCustomUser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class GetFailedOperationsTest extends BaseIdmResourceTest {

  @Test
  public void testGetFailedOperations() throws Exception {
    userLogRepository.deleteAll();
    LocalDateTime log1time = LocalDateTime.of(2018, 1, 1, 12, 0, 15);
    LocalDateTime log0time = log1time.minusHours(4).plusMinutes(13);
    LocalDateTime log2time = log1time.plusMinutes(10);
    LocalDateTime log3time = log2time.plusMinutes(10).minusSeconds(15);
    LocalDateTime log4time = log3time.plusMonths(1).minusHours(7);
    LocalDateTime log5time = log4time.plusWeeks(2).minusHours(6).plusMinutes(18);

    userLog(USER_WITH_RACFID_AND_DB_DATA_ID, CREATE, log0time);
    userLog("this-id-should-be-unused", CREATE, log1time);
    userLog(USER_NO_RACFID_ID, CREATE, log2time);
    userLog(USER_WITH_RACFID_ID, CREATE, log3time);
    userLog(USER_WITH_RACFID_ID, UPDATE, log4time);
    userLog(USER_WITH_RACFID_AND_DB_DATA_ID, UPDATE, log5time);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(
                    "/idm/users/failed-operations?date=" + getDateString(log1time))
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();
    System.out.println(result.getResponse().getContentAsString());

    assertNonStrict(result, "fixtures/idm/failed-operations/failed-operations-valid.json");

    Iterable<UserLog> userLogs = userLogRepository.findAll();
    int newUserLogsSize = Iterables.size(userLogs);
    assertThat(newUserLogsSize, is(4));

    Iterator<UserLog> it = userLogs.iterator();
    assertUserLog(it, USER_NO_RACFID_ID, CREATE, log2time);
    assertUserLog(it, USER_WITH_RACFID_ID, CREATE, log3time);
    assertUserLog(it, USER_WITH_RACFID_ID, UPDATE, log4time);
    assertUserLog(it, USER_WITH_RACFID_AND_DB_DATA_ID, UPDATE, log5time);
  }

  @Test
  @WithMockCustomUser
  public void testGetFailedOperationsNoBasicAuth() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                "/idm/users/failed-operations?date=" + getDateString(LocalDateTime.now())))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  public void testGetFailedOperationsInvalidDateFormat() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/idm/users/failed-operations?date=2018-08-01-13.26.33")
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
    assertExtensible(result, "fixtures/idm/failed-operations/failed-operations-invalid-date.json");
  }

  private UserLog userLog(String userName, OperationType operation, LocalDateTime dateTime) {
    UserLog log = new UserLog();
    log.setUsername(userName);
    log.setOperationType(operation);
    log.setOperationTime(dateTime);
    return userLogRepository.save(log);
  }

  private void assertUserLog(
      Iterator<UserLog> iterator, String username, OperationType operationType,
      LocalDateTime time) {
    UserLog userLog = iterator.next();
    assertUserLog(userLog, username, operationType, time);
  }

  private void assertUserLog(
      UserLog userLog, String username, OperationType operationType, LocalDateTime time) {
    assertThat(userLog.getUsername(), is(username));
    assertThat(userLog.getOperationType(), is(operationType));
    assertThat(userLog.getOperationTime(), is(time));
  }

  private static String getDateString(LocalDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN);
    return date.format(formatter);
  }
}
