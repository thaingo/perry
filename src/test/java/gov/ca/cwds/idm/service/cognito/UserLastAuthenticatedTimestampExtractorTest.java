package gov.ca.cwds.idm.service.cognito;

import static gov.ca.cwds.idm.service.cognito.UserLastAuthenticatedTimestampExtractor.extractUserLastAuthenticatedTimestamp;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.amazonaws.services.cognitoidp.model.AdminListDevicesResult;
import com.amazonaws.services.simpleworkflow.flow.JsonDataConverter;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.Test;

public class UserLastAuthenticatedTimestampExtractorTest {

  private JsonDataConverter jsonDataConverter = new JsonDataConverter();

  @Test
  public void extractUserLastAuthenticatedTimestampNullDevices() throws IOException {
    assertFalse(extractUserLastAuthenticatedTimestamp(
        getAdminListDevicesResult("fixtures/idm/devices/null_devices.json")).isPresent());
  }

  @Test
  public void extractUserLastAuthenticatedTimestampEmptyDevices() throws IOException {
    assertFalse(extractUserLastAuthenticatedTimestamp(
        getAdminListDevicesResult("fixtures/idm/devices/no_devices.json")).isPresent());
  }

  @Test
  public void extractUserLastAuthenticatedTimestampTwoDevices() throws Exception {
    assertEquals(LocalDateTime.of(2018, 9, 17, 05, 1,
        12)/*new SimpleDateFormat(DATE_FORMAT_PATTERN).parse("2018-09-17 05:01:12")*/,
        extractUserLastAuthenticatedTimestamp(
            getAdminListDevicesResult("fixtures/idm/devices/two_devices.json")).get());
  }

  private AdminListDevicesResult getAdminListDevicesResult(String fixturePath) throws IOException {
    return CognitoObjectMapperHolder.OBJECT_MAPPER
        .readValue(fixture(fixturePath), AdminListDevicesResult.class);
  }

}