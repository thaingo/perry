package gov.ca.cwds.data.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class StringToRequiredLongConverterTest {
  private StringToRequiredLongConverter converter = new StringToRequiredLongConverter();

  @Test
  public void testConvertingToEntity() {
    assertNull(converter.convertToEntityAttribute(0L));
    assertNull(converter.convertToEntityAttribute(null));
    assertEquals("9165559377", converter.convertToEntityAttribute(9165559377L));
  }

  @Test
  public void testConvertingToDbColumn() {
    assertEquals(Long.valueOf(0L), converter.convertToDatabaseColumn(null));
    assertEquals(Long.valueOf(0L), converter.convertToDatabaseColumn(""));
    assertEquals(Long.valueOf(9165559377L), converter.convertToDatabaseColumn("9165559377"));
  }
}
