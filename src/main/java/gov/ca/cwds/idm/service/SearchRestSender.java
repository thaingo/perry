package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("idm")
public class SearchRestSender {

  @Autowired private RestTemplate restTemplate;

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "${perry.doraWsMaxAttempts}",
      backoff = @Backoff(delayExpression = "${perry.doraWsRetryTimeoutMs}"))
  public ResponseEntity<String> send(
      String urlTemplate, HttpEntity<User> requestEntity, Map<String, String> params) {
    return restTemplate.exchange(urlTemplate, HttpMethod.PUT, requestEntity, String.class, params);
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
}
