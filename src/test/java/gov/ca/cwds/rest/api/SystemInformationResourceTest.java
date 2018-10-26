package gov.ca.cwds.rest.api;

import static gov.ca.cwds.config.Constants.HTTP_STATUS_NOT_HEALTHY;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.dto.app.SystemInformationDto;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class SystemInformationResourceTest {

  private ObjectMapper objectMapper = Jackson.newObjectMapper();

  @Mock
  private InfoEndpoint infoEndpoint;

  @Mock
  private HealthEndpoint healthEndpoint;

  @InjectMocks
  private SystemInformationResource systemInformationResource;

  private SystemInformationResource spySystemInformationResource;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);

    Map<String, Object> mockInfo = readJsonAsMap("mocks/rest/api/mock-spring-info.json");
    given(infoEndpoint.invoke()).willReturn(mockInfo);

    spySystemInformationResource = spy(systemInformationResource);
    when(spySystemInformationResource.objectToHealth(any()))
        .thenAnswer(i -> objectToMockHealth(i.getArguments()[0]));
  }

  @Test
  public void testNotHealthy() throws IOException {
    initMockHealth("mocks/rest/api/mock-spring-health-down.json");
    ResponseEntity<SystemInformationDto> responseEntity = spySystemInformationResource.getInfo();
    assertEquals(HTTP_STATUS_NOT_HEALTHY, responseEntity.getStatusCodeValue());

    String actualJson = objectMapper.writeValueAsString(responseEntity.getBody());
    String expectedJSON = fixture("fixtures/rest/api/system-information-unhealthy.json");
    assertEquals(objectMapper.readTree(expectedJSON), objectMapper.readTree(actualJson));
  }

  @Test
  public void testHealthy() throws IOException {
    initMockHealth("mocks/rest/api/mock-spring-health-up.json");
    ResponseEntity<SystemInformationDto> responseEntity = spySystemInformationResource.getInfo();
    assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

    String actualJson = objectMapper.writeValueAsString(responseEntity.getBody());
    String expectedJSON = fixture("fixtures/rest/api/system-information-healthy.json");
    assertEquals(objectMapper.readTree(expectedJSON), objectMapper.readTree(actualJson));
  }

  private Map<String, Object> readJsonAsMap(String path) throws IOException {
    return objectMapper.readValue(fixture(path), new TypeReference<Map<String, Object>>() {
    });
  }

  private void initMockHealth(String path) throws IOException {
    Map<String, Object> mockHealthData = readJsonAsMap(path);
    Health mockHealth = objectToMockHealth(mockHealthData);
    given(healthEndpoint.invoke()).willReturn(mockHealth);
  }

  @SuppressWarnings("unchecked")
  private Health objectToMockHealth(Object obj) {
    Map<String, Object> data = (Map<String, Object>) obj;
    Map<String, Object> statusData = (Map<String, Object>) data.get("status");
    Map<String, Object> detailsData = (Map<String, Object>) data.get("details");

    Health health = Mockito.mock(Health.class);
    given(health.getStatus()).willReturn(new Status(statusData.get("code").toString()));
    given(health.getDetails()).willReturn(detailsData);

    return health;
  }
}
