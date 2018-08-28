package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("idm")
public class SearchRestSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchRestSender.class);

  @Autowired private RestTemplate restTemplate;

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "${perry.doraWsMaxAttempts}",
      backoff = @Backoff(delayExpression = "${perry.doraWsRetryDelayMs}"))
  public ResponseEntity<String> send(
      String urlTemplate, HttpEntity<User> requestEntity, Map<String, String> params) {

    try {
      return restTemplate.exchange(urlTemplate, HttpMethod.PUT, requestEntity, String.class, params);
    } catch (RestClientException e) {
      logException(e);
      throw e;
    }
  }

  private void logException(RestClientException e) {
    LOGGER.error("Dora access error, type: {}", e.getClass().getSimpleName());

    if (e instanceof RestClientResponseException) {
      RestClientResponseException restClientResponseException = (RestClientResponseException) e;
      LOGGER.error(
          "error response body: {}", restClientResponseException.getResponseBodyAsString());
      LOGGER.error("error response headers: {}", restClientResponseException.getResponseHeaders());

      if (restClientResponseException instanceof HttpStatusCodeException) {
        HttpStatusCodeException httpStatusCodeException =
            (HttpStatusCodeException) restClientResponseException;
        LOGGER.error("error response status: {}", httpStatusCodeException.getStatusCode());
      }
    }
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
}
