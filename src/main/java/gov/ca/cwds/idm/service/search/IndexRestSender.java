package gov.ca.cwds.idm.service.search;

import gov.ca.cwds.idm.service.cognito.SearchProperties;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("idm")
public class IndexRestSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexRestSender.class);

  @Autowired
  private RestTemplateBuilder restTemplateBuilder;

  @Autowired
  @Qualifier("indexRetryTemplate")
  private RetryTemplate retryTemplate;

  @Autowired
  private SearchProperties searchProperties;

  private RestTemplate restTemplate;

  @PostConstruct
  public void init() {
    String basicAuthUser = searchProperties.getDoraBasicAuthUser();
    String basicAuthPass = searchProperties.getDoraBasicAuthPass();
    restTemplate = restTemplateBuilder.basicAuthorization(basicAuthUser,basicAuthPass).build();
  }

  public ResponseEntity<String> send(
      String urlTemplate, HttpEntity<?> requestEntity, Map<String, String> params) {

    return retryTemplate.execute(
        context -> {
          int retryCount = context.getRetryCount();
          if (retryCount > 0) {
            LOGGER.info("Attempt to retry Search system invocation, number {}:", retryCount);
          }
          return restTemplate.exchange(
              urlTemplate, HttpMethod.PUT, requestEntity, String.class, params);
        });
  }

  void setRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplateBuilder = restTemplateBuilder;
  }

  void setRetryTemplate(RetryTemplate retryTemplate) {
    this.retryTemplate = retryTemplate;
  }

  void setSearchProperties(SearchProperties searchProperties) {
    this.searchProperties = searchProperties;
  }

  RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
}
