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
import gov.ca.cwds.idm.service.diff.Differencing;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserUpdateAttributesBuilder {

  private final Differencing differencing;
  private final List<AttributeType> attributeTypes = new ArrayList<>();

  public UserUpdateAttributesBuilder(Differencing differencing) {
    this.differencing = differencing;
  }

  public List<AttributeType> build() {
    addEmailAttributes(differencing.getEmailDiff());
    addPhoneAttribute(differencing.getPhoneNumberDiff());
    addStringAttribute(PHONE_EXTENSION, differencing.getPhoneExtensionNumberDiff());
    addStringSetAttribute(PERMISSIONS, differencing.getPermissionsDiff());
    addStringSetAttribute(ROLES, differencing.getRolesDiff());
    return attributeTypes;
  }

  public void addStringAttribute(
      UserAttribute userAttribute, Optional<StringDiff> optDiff) {
    optDiff.ifPresent(diff -> addStringAttribute(userAttribute, diff.getNewValue()));
  }

  public UserUpdateAttributesBuilder addStringSetAttribute(
      UserAttribute userAttribute, Optional<StringSetDiff> optDiff) {
    optDiff.ifPresent(
        diff -> attributeTypes.add(createDelimitedAttribute(userAttribute, diff.getNewValue())));
    return this;
  }

  public void addEmailAttributes(Optional<StringDiff> optEmailDiff) {
    optEmailDiff.ifPresent(
        diff -> {
          addStringAttribute(EMAIL, diff.getNewValue());
          addStringAttribute(EMAIL_VERIFIED, TRUE_VALUE);
        }
    );
  }

  public void addPhoneAttribute(Optional<StringDiff> optDiff) {
    optDiff.ifPresent(diff -> addStringAttribute(PHONE_NUMBER, toCognitoFormat(diff.getNewValue())));
  }

  private void addStringAttribute(UserAttribute userAttribute, String value) {
    attributeTypes.add(new AttributeType().withName(userAttribute.getName()).withValue(value));
  }


}
