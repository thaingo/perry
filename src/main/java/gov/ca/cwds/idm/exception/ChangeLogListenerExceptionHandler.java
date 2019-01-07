package gov.ca.cwds.idm.exception;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class ChangeLogListenerExceptionHandler implements AsyncUncaughtExceptionHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ChangeLogListenerExceptionHandler.class);

  @Override
  public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
    LOGGER.error("Unhandled Exception thrown by {} caused by {}", method.getName(),
        throwable.getMessage());
  }
}
