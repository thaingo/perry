package gov.ca.cwds.idm.service.cognito;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Alexander Serbin on 9/18/2018
 */
public class CognitoObjectMapperHolder {

  public static final ObjectMapper OBJECT_MAPPER = Jackson.getObjectMapper();

  static {
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
    String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    OBJECT_MAPPER.setDateFormat(df);
  }

}
