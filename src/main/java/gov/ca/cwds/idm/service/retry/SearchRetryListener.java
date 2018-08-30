package gov.ca.cwds.idm.service.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientResponseException;

public class SearchRetryListener extends RetryListenerSupport {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchRetryListener.class);

  @Override
  public <T, E extends Throwable> void onError(
      RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {

    LOGGER.error("Search system access error, type: {}", throwable.getClass().getSimpleName());

    if (throwable instanceof RestClientResponseException) {
      RestClientResponseException restClientResponseException =
          (RestClientResponseException) throwable;
      LOGGER.error(
          "Search system error response body: {}", restClientResponseException.getResponseBodyAsString());
      LOGGER.error("Search system error response headers: {}", restClientResponseException.getResponseHeaders());

      if (restClientResponseException instanceof HttpStatusCodeException) {
        HttpStatusCodeException httpStatusCodeException =
            (HttpStatusCodeException) restClientResponseException;
        LOGGER.error("Search system error response status: {}", httpStatusCodeException.getStatusCode());
      }
    }
    super.onError(context, callback, throwable);
  }
}
