package gov.ca.cwds.idm.service.retry;

import gov.ca.cwds.PerryProperties;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;

@Configuration
@Profile("idm")
@EnableRetry
public class SearchRetryConfiguration {

  private PerryProperties properties;

  @Bean(name = "searchRetryTemplate")
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(properties.getDoraWsRetryDelayMs());
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

    SimpleRetryPolicy retryPolicy =
        new SimpleRetryPolicy(
            properties.getDoraWsMaxAttempts(),
            Collections.singletonMap(RestClientException.class, Boolean.TRUE));
    retryTemplate.setRetryPolicy(retryPolicy);

    retryTemplate.registerListener(searchRetryListener());

    return retryTemplate;
  }

  @Bean
  public RetryListener searchRetryListener() {
    return new SearchRetryListener();
  }

  @Autowired
  public void setProperties(PerryProperties properties) {
    this.properties = properties;
  }
}
