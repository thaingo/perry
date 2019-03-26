package gov.ca.cwds.idm.service.search;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.IndexRestSender;
import gov.ca.cwds.idm.service.cognito.SearchProperties;
import gov.ca.cwds.idm.service.cognito.SearchProperties.SearchIndexProperties;
import gov.ca.cwds.idm.service.retry.IndexRetryConfiguration;
import gov.ca.cwds.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class UserIndexServiceTest {

  private static final String USER_ID = "123";
  private static final String USER_ERROR_ID = "999";
  private static final String DORA_RESPONSE = "{\"_id\": \"123\"\"}";
  public static final String BASIC_AUTH_USER = "ba_user";
  public static final String BASIC_AUTH_PWD = "ba_pwd";
  protected static final String BASIC_AUTH_HEADER = Utils.prepareBasicAuthHeader(BASIC_AUTH_USER,
      BASIC_AUTH_PWD);

  private UserIndexService service;

  private MockRestServiceServer mockServer;

  @Before
  public void before() {
    service = new UserIndexService();

    PerryProperties perryProperties = new PerryProperties();
    perryProperties.setDoraWsMaxAttempts(3);
    perryProperties.setDoraWsRetryDelayMs(500);

    SearchProperties searchProperties = new SearchProperties();
    searchProperties.setDoraUrl("http://localhost");
    searchProperties.setDoraBasicAuthUser("ba_user");
    searchProperties.setDoraBasicAuthPass("ba_pwd");

    SearchIndexProperties usersIndex = new SearchIndexProperties();
    searchProperties.setUsersIndex(usersIndex);
    usersIndex.setName("users");
    usersIndex.setType("user");

    service.setSearchProperties(searchProperties);

    IndexRetryConfiguration indexRetryConfiguration = new IndexRetryConfiguration();
    indexRetryConfiguration.setProperties(perryProperties);

    RestTemplate restTemplate = new RestTemplate();
    IndexRestSender restSender = new IndexRestSender();
    restSender.setRestTemplate(restTemplate);
    restSender.setRetryTemplate(indexRetryConfiguration.retryTemplate());

    service.setRestSender(restSender);

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
        .expect(requestTo("http://localhost/dora/users/user/" + USER_ID))
        .andExpect(method(HttpMethod.PUT))
        .andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
        .andRespond(withSuccess(DORA_RESPONSE, MediaType.APPLICATION_JSON));

    User user = new User();
    user.setId(USER_ID);
    ResponseEntity<String> response = service.updateUserInIndex(user);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody(), is(DORA_RESPONSE));
  }

  @Test
  public void testCreate() {
    mockServer
        .expect(
            requestTo(
                "http://localhost/dora/users/user/" + USER_ID + "/_create"))
        .andExpect(method(HttpMethod.PUT))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
        .andRespond(
            request -> new MockClientHttpResponse(DORA_RESPONSE.getBytes(), HttpStatus.CREATED));

    User user = new User();
    user.setId(USER_ID);
    ResponseEntity<String> response = service.createUserInIndex(user);
    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(response.getBody(), is(DORA_RESPONSE));
  }

  @Test(expected = HttpServerErrorException.class)
  public void testDoraError() {
    mockServer
        .expect(times(3), requestTo("http://localhost/dora/users/user/" + USER_ERROR_ID))
        .andExpect(method(HttpMethod.PUT))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
        .andRespond(
        request -> new MockClientHttpResponse("error".getBytes(), HttpStatus.INTERNAL_SERVER_ERROR));

    User user = new User();
    user.setId(USER_ERROR_ID);
    service.updateUserInIndex(user);
  }
}
