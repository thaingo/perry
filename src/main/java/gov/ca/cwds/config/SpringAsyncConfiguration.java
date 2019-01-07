package gov.ca.cwds.config;

import gov.ca.cwds.idm.exception.ChangeLogListenerExceptionHandler;
import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Profile("idm")
@Configuration
@EnableAsync
public class SpringAsyncConfiguration implements AsyncConfigurer {

  @Override
  @Bean(name = "auditListenerTaskExecutor")
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("ChangeLogExecutor-");
    executor.initialize();

    return new DelegatingSecurityContextAsyncTaskExecutor(executor);
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new ChangeLogListenerExceptionHandler();
  }
}
