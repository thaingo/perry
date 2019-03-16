package gov.ca.cwds.idm.service.cognito.util;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.COUNTY;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.OFFICE;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.FIRST_NAME;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.LAST_NAME;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.util.Utils.toUpperCase;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.attribute.AttributesBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.UserAttribute;
import java.util.List;
import java.util.Optional;
import org.springframework.util.CollectionUtils;

public class CognitoUtils {

  public static final String TRUE_VALUE = "True";

  private CognitoUtils() {
  }

  static Optional<AttributeType> getAttribute(UserType cognitoUser, String attrName) {
    List<AttributeType> attributes = cognitoUser.getAttributes();

    if (CollectionUtils.isEmpty(attributes)) {
      return Optional.empty();
    } else {
      return attributes
          .stream()
          .filter(attr -> attr.getName().equalsIgnoreCase(attrName))
          .findFirst();
    }
  }

  public static String getAttributeValue(UserType cognitoUser, String attributeName) {
    return getAttribute(cognitoUser, attributeName).map(AttributeType::getValue).orElse(null);
  }

  public static String getAttributeValue(UserType cognitoUser, UserAttribute userAttribute) {
    return getAttributeValue(cognitoUser, userAttribute.getName());
  }

  public static String getEmail(UserType cognitoUser) {
    return getAttributeValue(cognitoUser, EMAIL.getName());
  }

  public static List<AttributeType> buildCreateUserAttributes(User user) {

    String racfid = toUpperCase(user.getRacfid());

    AttributesBuilder attributesBuilder =
        new AttributesBuilder()
            .addAttribute(EMAIL, user.getEmail())
            .addAttribute(FIRST_NAME, user.getFirstName())
            .addAttribute(LAST_NAME, user.getLastName())
            .addAttribute(COUNTY, user.getCountyName())
            .addAttribute(OFFICE, user.getOfficeId())
            .addAttribute(RACFID_STANDARD, racfid)
            .addAttribute(EMAIL_VERIFIED, TRUE_VALUE)
        ;
    return attributesBuilder.build();
  }

  public static String getRACFId(UserType user) {
    return CognitoUtils.getAttributeValue(user, RACFID_STANDARD.getName());
  }
}
