package gov.ca.cwds.service.sso.retry;

import gov.ca.cwds.PerryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Configuration
@Profile("prod")
@EnableRetry
public class IdpRetryConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdpRetryConfiguration.class);

  private PerryProperties properties;

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(properties.getIdpRetryTimeout());
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy() {
      @Override
      public void close(RetryContext status) {
        if(status.getLastThrowable() != null
            && status.getLastThrowable() instanceof HttpClientErrorException) {
          HttpClientErrorException exception = (HttpClientErrorException) status.getLastThrowable();
          LOGGER.error("IDP ACCESS ERROR!");
          LOGGER.error("ERROR RESPONSE STATUS: {}", exception.getStatusCode());
          LOGGER.error("ERROR RESPONSE BODY: {}", exception.getResponseBodyAsString());
          LOGGER.error("ERROR RESPONSE HEADERS: {}", exception.getResponseHeaders());
        }
      }
    };
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
