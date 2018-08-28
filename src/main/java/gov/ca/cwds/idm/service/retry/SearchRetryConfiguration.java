package gov.ca.cwds.idm.service.retry;

import gov.ca.cwds.PerryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@Profile("idm")
@EnableRetry
public class SearchRetryConfiguration {

  private PerryProperties properties;

  private RetryListener searchRetryListener;

  @Bean(name = "searchRetryTemplate")
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(properties.getDoraWsRetryDelayMs());
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(properties.getDoraWsMaxAttempts());
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
