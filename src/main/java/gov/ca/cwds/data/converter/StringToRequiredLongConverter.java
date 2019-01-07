package gov.ca.cwds.data.converter;

import javax.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

/**
 *     <p>This converter converts string value to long and use zero as default value
 */
public class StringToRequiredLongConverter implements AttributeConverter<String, Long> {

  @Override
  public Long convertToDatabaseColumn(String string) {
    if (StringUtils.isBlank(string)) {
      return 0L;
    }
    return Long.valueOf(string);
  }

  @Override
  public String convertToEntityAttribute(Long dbData) {
    if (dbData == null || dbData == 0) {
      return null;
    }
    return String.valueOf(dbData);
  }
}
