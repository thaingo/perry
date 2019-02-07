package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PHONE_EXTENSION;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.idm.service.cognito.util.CognitoPhoneConverter.toCognitoFormat;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.TRUE_VALUE;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.createDelimitedAttribute;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserUpdateAttributesUtil {

  private UserUpdateAttributesUtil() {
  }

  public static List<AttributeType> buildUpdatedAttributesList(UpdateDifference updateDifference) {
    List<AttributeType> attrs = new ArrayList<>();

    addEmailAttributes(updateDifference.getEmailDiff(), attrs);
    addPhoneAttribute(updateDifference.getPhoneNumberDiff(), attrs);
    addStringAttribute(PHONE_EXTENSION, updateDifference.getPhoneExtensionNumberDiff(), attrs);
    addStringSetAttribute(PERMISSIONS, updateDifference.getPermissionsDiff(), attrs);
    addStringSetAttribute(ROLES, updateDifference.getRolesDiff(), attrs);
    return attrs;
  }

  private static void addStringAttribute(
      UserAttribute userAttribute, Optional<StringDiff> optDiff, List<AttributeType> attrs) {
    optDiff.ifPresent(diff -> addStringAttribute(userAttribute, diff.getNewValue(), attrs));
  }

  private static void addStringSetAttribute(
      UserAttribute userAttribute, Optional<StringSetDiff> optDiff, List<AttributeType> attrs) {
    optDiff.ifPresent(
        diff -> attrs.add(createDelimitedAttribute(userAttribute, diff.getNewValue())));
  }

  private static void addEmailAttributes(Optional<StringDiff> optEmailDiff, List<AttributeType> attrs) {
    optEmailDiff.ifPresent(
        diff -> {
          addStringAttribute(EMAIL, diff.getNewValue(), attrs);
          addStringAttribute(EMAIL_VERIFIED, TRUE_VALUE, attrs);
        }
    );
  }

  private static void addPhoneAttribute(Optional<StringDiff> optDiff, List<AttributeType> attrs) {
    optDiff.ifPresent(
        diff -> addStringAttribute(PHONE_NUMBER, toCognitoFormat(diff.getNewValue()), attrs));
  }

  private static void addStringAttribute(UserAttribute userAttribute, String value,
      List<AttributeType> attrs) {
    attrs.add(new AttributeType().withName(userAttribute.getName()).withValue(value));
  }
}
