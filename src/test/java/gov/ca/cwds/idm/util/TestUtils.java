package gov.ca.cwds.idm.util;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Date;

public final class TestUtils {

  private TestUtils() {}

  public static Date date(int year, int month, int dayOfMonth) {
    return java.sql.Date.valueOf((LocalDate.of(year, month, dayOfMonth)));
  }

  public static AttributeType attr(String name, String value) {
    AttributeType attr = new AttributeType();
    attr.setName(name);
    attr.setValue(value);
    return attr;
  }

  public static String asJsonString(final Object obj) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JavaTimeModule javaTimeModule = new JavaTimeModule();
      objectMapper.registerModule(javaTimeModule);
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
