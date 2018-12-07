package gov.ca.cwds.util;

import static gov.ca.cwds.config.Constants.DEFAULT_LOCALE;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getRACFId;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.actuate.health.Status;

public class Utils {

  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String TIME_FORMAT = "HH:mm:ss";
  public static final String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
  public static final DateTimeFormatter DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

  public static final String URL_DATETIME_FORMAT_PATTERN = "yyyy-MM-dd-HH.mm.ss.SSS";
  public static final DateTimeFormatter URL_DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern(URL_DATETIME_FORMAT_PATTERN);

  private static final String HEALTHY_CODE = "UP";

  private Utils() {
  }

  @SuppressWarnings("unchecked")
  public static <T> T deserialize(byte[] data) {
    return (T) SerializationUtils.deserialize(data);
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
    if (str != null) {
      return str.toUpperCase(DEFAULT_LOCALE);
    } else {
      return null;
    }
  }

  public static String toLowerCase(String str) {
    if (str != null) {
      return str.toLowerCase(DEFAULT_LOCALE);
    } else {
      return null;
    }
  }

  @SuppressWarnings({"squid:S1319"})//need HashSet in tests
  public static <T> HashSet<T> toSet(T... values) {
    return new HashSet<>(Arrays.asList(values));
  }


  public static String formatDate(LocalDate date) {
    if (date != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
      return date.format(formatter);
    }
    return null;
  }

  public static boolean isRacfidUser(User user) {
    return StringUtils.isNotBlank(user.getRacfid());
  }

  public static boolean isRacfidUser(UserType userType) {
    return StringUtils.isNotBlank(getRACFId(userType));
  }

  public static boolean isStatusHealthy(Status status) {
    return HEALTHY_CODE.equals(status.getCode());
  }


  /**
   * converts time strings for correct /system-information response
   *
   * @param ht a String with Spring healthcheck time formatted like "2018-10-22 22:59:43+0000"
   * @return a String with local time formatted like "2018-10-18T14:27:16.505-07:00"
   */
  public static String healthCheckUtcTimeToPacific(String ht) {
    DateTimeFormatter formatterIn = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ");
    DateTimeFormatter formatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS[XXX]");
    ZonedDateTime dt = ZonedDateTime.parse(ht, formatterIn)
        .withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
    return dt.format(formatterOut);
  }

  public static String toCommaDelimitedString(Collection<String> collection) {
    if (collection == null) {
      return null;
    }
    return "[" + String.join(", ", collection) + "]";
  }
}
