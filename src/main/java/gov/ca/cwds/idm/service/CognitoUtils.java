package gov.ca.cwds.idm.service;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import liquibase.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;

public class CognitoUtils {

  static final String PERMISSIONS_ATTR_NAME = "custom:permission";
  static final String COUNTY_ATTR_NAME = "custom:County";
  static final String PERMISSIONS_DELIMITER = ":";

  private CognitoUtils() {
  }

  public static Optional<AttributeType> getAttribute(UserType cognitoUser, String attrName) {
    List<AttributeType> attributes = cognitoUser.getAttributes();

    if(CollectionUtils.isEmpty(attributes)) {
      return Optional.empty();
    } else {
      return attributes.stream().filter(attr -> attr.getName().equals(attrName))
          .findFirst();
    }
  }

  public static String getCountyName(UserType cognitoUser) {
    return getAttribute(cognitoUser, COUNTY_ATTR_NAME)
        .map(attr -> attr.getValue()).orElse(null);
  }

  public static Set<String> getPermissions(UserType cognitoUser) {

    Optional<AttributeType> permissionsAttrOpt = getAttribute(cognitoUser, PERMISSIONS_ATTR_NAME);

    if(! permissionsAttrOpt.isPresent()) {
      return new HashSet<>();
    }

    AttributeType permissionsAttr = permissionsAttrOpt.get();
    String permissionsStr = permissionsAttr.getValue();

    if(StringUtils.isEmpty(permissionsStr)){
      return new HashSet<>();
    }

    return new HashSet<>(Arrays.asList(permissionsStr.split(PERMISSIONS_DELIMITER)));
  }

  public static String getPermissionsAttributeValue(Set<String> permissions) {
    if(CollectionUtils.isNotEmpty(permissions)) {
      return String.join(PERMISSIONS_DELIMITER, permissions);
    } else {
      return "";
    }
  }

  public static AttributeType createPermissionsAttribute(Set<String> permissions) {
    AttributeType permissionsAttr = new AttributeType();
    permissionsAttr.setName(PERMISSIONS_ATTR_NAME);
    permissionsAttr.setValue(getPermissionsAttributeValue(permissions));
    return permissionsAttr;
  }
}
