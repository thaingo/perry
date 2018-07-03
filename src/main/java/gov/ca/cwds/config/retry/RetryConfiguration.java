package gov.ca.cwds.config.retry;

import gov.ca.cwds.PerryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@Profile("prod")
@EnableRetry
public class RetryConfiguration {

  private PerryProperties properties;

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(properties.getIdpRetryTimeout());
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(properties.getIdpMaxAttempts());
    retryTemplate.setRetryPolicy(retryPolicy);

    return retryTemplate;
  }

  @Bean("retryInterceptor")
  public RetryOperationsInterceptor retryInterceptor() {
    RetryOperationsInterceptor interceptor = new RetryOperationsInterceptor();
    interceptor.setRetryOperations(retryTemplate());
    return interceptor;
  }

  @Autowired
  public void setProperties(PerryProperties properties) {
    this.properties = properties;
  }
}
