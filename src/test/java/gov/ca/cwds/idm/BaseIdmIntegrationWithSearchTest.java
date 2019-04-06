package gov.ca.cwds.idm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.ca.cwds.idm.service.AuditEventService;
import gov.ca.cwds.idm.service.search.IndexRestSender;
import gov.ca.cwds.idm.service.cognito.SearchProperties;
import gov.ca.cwds.idm.service.search.UserIndexService;
import gov.ca.cwds.idm.service.search.UserSearchService;
import java.util.Map;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public abstract class BaseIdmIntegrationWithSearchTest extends BaseIdmIntegrationTest {

  @Autowired
  protected UserIndexService userIndexService;

  @Autowired
  protected IndexRestSender indexRestSender;

  @MockBean
  protected AuditEventService auditEventService;

  @Autowired
  protected UserSearchService userSearchService;

  protected UserIndexService spyUserIndexService;

  protected RestTemplate mockRestTemplate = mock(RestTemplate.class);

  @Before
  public void before() {
    super.before();

    indexRestSender.setRestTemplate(mockRestTemplate);
    spyUserIndexService = spy(userIndexService);
    userSearchService.setUserIndexService(spyUserIndexService);
  }

  protected final void setDoraSuccess() {
    when(mockRestTemplate.exchange(
        any(String.class),
        any(HttpMethod.class),
        any(HttpEntity.class),
        any(Class.class),
        any(Map.class)))
        .thenReturn(ResponseEntity.ok().body("{\"success\":true}"));
  }

  protected final void setDoraError() {
    doThrow(new RestClientException("Elastic Search error"))
        .when(mockRestTemplate).exchange(any(String.class), any(HttpMethod.class),
        any(HttpEntity.class), any(Class.class), any(Map.class));
  }

  protected final void verifyDoraCalls(int times) {
    verify(mockRestTemplate, times(times)).exchange(
        any(String.class),
        any(HttpMethod.class),
        any(HttpEntity.class),
        any(Class.class),
        any(Map.class));
  }
}
