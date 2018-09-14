package gov.ca.cwds.idm.util;

import com.amazonaws.services.cognitoidp.model.AttributeType;
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

}
