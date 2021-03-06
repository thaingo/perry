package gov.ca.cwds.idm.util;

import static gov.ca.cwds.util.Utils.DATETIME_FORMATTER;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

public final class TestUtils {

  private static ObjectMapper OBJECT_MAPPER;

  static {
    OBJECT_MAPPER = new ObjectMapper();
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule
        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATETIME_FORMATTER));
    OBJECT_MAPPER.registerModule(javaTimeModule);
  }

  private TestUtils() {}


  public static Date date(int year, int month, int dayOfMonth) {
    return java.sql.Date.valueOf((LocalDate.of(year, month, dayOfMonth)));
  }

  public static AttributeType attr(UserAttribute userAttribute, String value) {
    AttributeType attr = new AttributeType();
    attr.setName(userAttribute.getName());
    attr.setValue(value);
    return attr;
  }

  public static String asJsonString(final Object obj) {
    try {
      return OBJECT_MAPPER.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T deserialize(String jsonStr, Class<T> clazz) {
    try {
      return OBJECT_MAPPER.readValue(jsonStr, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static LocalDateTime dateTime(long millis) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
  }

  public static long toMillis(LocalDateTime ldt) {
    return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  public static String generateId() {
    return UUID.randomUUID().toString();
  }

  public static String prepareBasicAuthHeader(String user, String pwd) {
    String authString = user + ":" + pwd;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes(StandardCharsets.UTF_8));
    String authStringEnc = new String(authEncBytes, StandardCharsets.UTF_8);
    return "Basic " + authStringEnc;
  }
}
