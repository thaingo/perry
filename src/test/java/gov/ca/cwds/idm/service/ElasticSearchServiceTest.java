package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.ElasticSearchService.getUrlTemplate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.model.OperationType;
import gov.ca.cwds.idm.service.cognito.ElasticSearchProperties;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.RestTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "gov.ca.cwds.util.CurrentAuthenticatedUserUtil")
public class ElasticSearchServiceTest {

  private static final String USER_ID = "123";
  private static final String SSO_TOKEN = "abc";
  private static final String DORA_RESPONSE = "{\"_id\": \"123\"\"}";

  private ElasticSearchService service;

  private MockRestServiceServer mockServer;

  @Before
  public void before() {
    mockStatic(CurrentAuthenticatedUserUtil.class);
    when(CurrentAuthenticatedUserUtil.getSsoToken()).thenReturn(SSO_TOKEN);

    service = new ElasticSearchService();

    ElasticSearchProperties properties = new ElasticSearchProperties();
    properties.setDoraUrl("http://localhost/dora");
    properties.setIndex("users");
    properties.setType("user");
    service.setElasticSearchProperties(properties);

    RestTemplate restTemplate = new RestTemplate();
    service.setRestTemplate(restTemplate);

    mockServer = MockRestServiceServer.bindTo(restTemplate).build();
  }

  @After
  public void after() {
    mockServer.verify();
    mockServer.reset();
  }

  @Test
  public void testUpdate() {
    mockServer
        .expect(requestTo("http://localhost/dora/users/user/" + USER_ID + "?token=" + SSO_TOKEN))
        .andExpect(method(HttpMethod.PUT))
        .andRespond(withSuccess(DORA_RESPONSE, MediaType.APPLICATION_JSON));

    User user = new User();
    user.setId(USER_ID);
    ResponseEntity<String> response = service.updateUser(user);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody(), is(DORA_RESPONSE));
  }

  @Test
  public void testCreate() {
    mockServer
        .expect(
            requestTo(
                "http://localhost/dora/users/user/" + USER_ID + "/_create?token=" + SSO_TOKEN))
        .andExpect(method(HttpMethod.PUT))
        .andRespond(
            request -> new MockClientHttpResponse(DORA_RESPONSE.getBytes(), HttpStatus.CREATED));

    User user = new User();
    user.setId(USER_ID);
    ResponseEntity<String> response = service.createUser(user);
    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(response.getBody(), is(DORA_RESPONSE));

    mockServer.verify();
    mockServer.reset();
  }

  @Test
  public void testGetUrlTemplate() {
    assertThat(
        getUrlTemplate(OperationType.CREATE),
        is("{doraUrl}/{esUserIndex}/{esUserType}/{id}/_create?token={ssoToken}"));
    assertThat(
        getUrlTemplate(OperationType.UPDATE),
        is("{doraUrl}/{esUserIndex}/{esUserType}/{id}?token={ssoToken}"));
  }
}
