package gov.ca.cwds.util;

import static gov.ca.cwds.config.Constants.DEFAULT_LOCALE;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class Utils {

  private Utils(){}

  public static <T> T deserialize(byte[] data) {
    return SerializationUtils.deserialize(data);
  }

  public static LocalDateTime fromDate(Date date) {
    return new Timestamp(date.getTime()).toLocalDateTime();
  }

  @SuppressWarnings("unchecked")
  public static <T> T unwrap(T bean) {
    try {
      if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
        Advised advised = (Advised) bean;
        return (T) advised.getTargetSource().getTarget();
      }
      return bean;
    } catch (Exception e) {
      return bean;
    }
  }

  public static String toUpperCase(String str) {
    if(str != null) {
      return str.toUpperCase(DEFAULT_LOCALE);
    } else {
      return null;
    }
  }

  public static String toLowerCase(String str) {
    if(str != null) {
      return str.toLowerCase(DEFAULT_LOCALE);
    } else {
      return null;
    }
  }
}
