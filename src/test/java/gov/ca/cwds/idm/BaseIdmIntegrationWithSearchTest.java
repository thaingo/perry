package gov.ca.cwds.idm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.ca.cwds.idm.service.SearchRestSender;
import gov.ca.cwds.idm.service.SearchService;
import gov.ca.cwds.idm.service.cognito.SearchProperties;
import java.util.Map;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public abstract class BaseIdmIntegrationWithSearchTest extends BaseIdmIntegrationTest {

  @Autowired
  protected SearchService searchService;

  @Autowired
  protected SearchRestSender searchRestSender;

  @Autowired
  protected SearchProperties searchProperties;

  protected SearchService spySearchService;

  protected RestTemplate mockRestTemplate = mock(RestTemplate.class);

  @Before
  public void before() {
    super.before();

    searchRestSender.setRestTemplate(mockRestTemplate);
    searchService.setRestSender(searchRestSender);
    searchService.setSearchProperties(searchProperties);
    spySearchService = spy(searchService);

    idmService.setSearchService(spySearchService);
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
