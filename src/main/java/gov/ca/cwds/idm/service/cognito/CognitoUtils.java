package gov.ca.cwds.idm.service.cognito;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CognitoUtils {

  public static final String PERMISSIONS_ATTR_NAME = "custom:Permission";
  public static final String ROLES_ATTR_NAME = "custom:Role";
  public static final String EMAIL_ATTR_NAME = "email";
  public static final String FIRST_NAME_ATTR_NAME = "given_name";
  public static final String LAST_NAME_ATTR_NAME = "family_name";
  public static final String PHONE_NUMBER_ATTR_NAME = "phone_number";
  public static final String OFFICE_ATTR_NAME = "custom:Office";
  public static final String EMAIL_DELIVERY = "EMAIL";
  public static final String COUNTY_ATTR_NAME = "custom:County";
  public static final String RACFID_ATTR_NAME = "preferred_username";
  private static final String COGNITO_LIST_DELIMITER = ":";

  private CognitoUtils() {}

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

  public static String getCountyName(UserType cognitoUser) {
    return getAttributeValue(cognitoUser, COUNTY_ATTR_NAME);
  }

  public static Set<String> getPermissions(UserType cognitoUser) {

    Optional<AttributeType> permissionsAttrOpt = getAttribute(cognitoUser, PERMISSIONS_ATTR_NAME);

    if (!permissionsAttrOpt.isPresent()) {
      return new HashSet<>();
    }

    AttributeType permissionsAttr = permissionsAttrOpt.get();
    String permissionsStr = permissionsAttr.getValue();

    if (StringUtils.isEmpty(permissionsStr)) {
      return new HashSet<>();
    }

    return new HashSet<>(Arrays.asList(permissionsStr.split(COGNITO_LIST_DELIMITER)));
  }

  public static String getCustomDelimeteredListAttributeValue(Set<String> setOfValues) {
    if (CollectionUtils.isNotEmpty(setOfValues)) {
      return String.join(COGNITO_LIST_DELIMITER, setOfValues);
    } else {
      return "";
    }
  }

  static AttributeType createPermissionsAttribute(Set<String> permissions) {
    return attribute(PERMISSIONS_ATTR_NAME, getCustomDelimeteredListAttributeValue(permissions));
  }

  static AttributeType createRolesAttribute(Set<String> roles) {
    return attribute(ROLES_ATTR_NAME, getCustomDelimeteredListAttributeValue(roles));
  }

  static AttributeType attribute(String name, String value) {
    AttributeType permissionsAttr = new AttributeType();
    permissionsAttr.setName(name);
    permissionsAttr.setValue(value);
    return permissionsAttr;
  }
}
