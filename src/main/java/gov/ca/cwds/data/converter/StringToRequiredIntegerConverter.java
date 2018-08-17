package gov.ca.cwds.data.converter;

import javax.persistence.AttributeConverter;

/**
 * @author CWDS TPT-3 Team
 *
 *
 * This converter converts string value to long and use zero as default value
 *
 */
public class StringToRequiredIntegerConverter implements AttributeConverter<String, Integer> {

  @Override
  public Integer convertToDatabaseColumn(String string) {
    if (string == null) {
      return 0;
    }
    return Integer.valueOf(string);
  }

  @Override
  public String convertToEntityAttribute(Integer dbData) {
    if (dbData == null || dbData == 0) {
      return null;
    }
    return String.valueOf(dbData);
  }

}
