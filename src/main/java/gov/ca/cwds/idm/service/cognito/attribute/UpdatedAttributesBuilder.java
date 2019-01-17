package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PHONE_EXTENSION;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.idm.service.cognito.util.CognitoPhoneConverter.toCognitoFormat;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.cognito.attribute.diff.CollectionAttributeDiffBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.diff.StringAttributeDiffBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.diff.StringUserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiffBuilder;
import gov.ca.cwds.idm.service.cognito.util.CognitoUtils;
import gov.ca.cwds.util.Utils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UpdatedAttributesBuilder {

  private final UserType existedCognitoUser;
  private final Map<UserAttribute, UserAttributeDiff> updatedAttributes = new LinkedHashMap<>();

  public UpdatedAttributesBuilder(UserType existedCognitoUser, UserUpdate updateUserDto) {
    this.existedCognitoUser = existedCognitoUser;
    addEmailDiff(updateUserDto.getEmail());
    addStringAttribute(PHONE_NUMBER, toCognitoFormat(updateUserDto.getPhoneNumber()));
    addStringAttribute(PHONE_EXTENSION, updateUserDto.getPhoneExtensionNumber());
    addCollectionAttribute(PERMISSIONS, updateUserDto.getPermissions());
    addCollectionAttribute(ROLES, updateUserDto.getRoles());
  }

  private void addEmailDiff(String email) {
    StringAttributeDiffBuilder emailAttributeDiffBuilder =
        new StringAttributeDiffBuilder(existedCognitoUser,
            EMAIL,
            Utils.toLowerCase(email));
    if (emailAttributeDiffBuilder.doesDiffExist()) {
      updatedAttributes.put(EMAIL, emailAttributeDiffBuilder.buildUserAttributeDiff());
      updatedAttributes.put(EMAIL_VERIFIED, new StringUserAttributeDiff(
          new AttributeType().withName(EMAIL_VERIFIED.getName()).withValue(CognitoUtils.TRUE_VALUE),
          "",
          CognitoUtils.TRUE_VALUE));
    }
  }

  public Map<UserAttribute, UserAttributeDiff> getUpdatedAttributes() {
    return updatedAttributes;
  }

  private void addStringAttribute(UserAttribute userAttribute, String newAttrValue) {
    UserAttributeDiffBuilder<String> diffBuilder =
        new StringAttributeDiffBuilder(existedCognitoUser, userAttribute, newAttrValue);
    updateAttributes(userAttribute, diffBuilder);
  }

  private void addCollectionAttribute(UserAttribute userAttribute, Set<String> newValues) {
    UserAttributeDiffBuilder<Set<String>> diffBuilder =
        new CollectionAttributeDiffBuilder(existedCognitoUser, userAttribute, newValues);
    updateAttributes(userAttribute, diffBuilder);
  }

  private <T> void updateAttributes(UserAttribute userAttribute,
      UserAttributeDiffBuilder<T> diffBuilder) {
    if (diffBuilder.doesDiffExist()) {
      updatedAttributes.put(userAttribute, diffBuilder.buildUserAttributeDiff());
    }
  }
}
