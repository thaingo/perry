package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PHONE_EXTENSION;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.idm.service.cognito.util.CognitoPhoneConverter.toCognitoFormat;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.buildEmailAttributes;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getAttributeValue;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UpdatedAttributesBuilder {

  private final UserType existedCognitoUser;
  private final Map<UserAttribute, AttributeType> updatedAttributes = new LinkedHashMap<>();
  private final StringAttributeHelper stringAttributeHelper = new StringAttributeHelper();
  private final CollectionAttributeHelper collectionAttributeHelper = new CollectionAttributeHelper();

  public UpdatedAttributesBuilder(UserType existedCognitoUser, UserUpdate updateUserDto) {
    this.existedCognitoUser = existedCognitoUser;

    addEmailAttributes(updateUserDto.getEmail());
    addAttribute(PHONE_NUMBER, toCognitoFormat(updateUserDto.getPhoneNumber()));
    addAttribute(PHONE_EXTENSION, updateUserDto.getPhoneExtensionNumber());
    addDelimitedAttribute(PERMISSIONS, updateUserDto.getPermissions());
    addDelimitedAttribute(ROLES, updateUserDto.getRoles());
  }

  public Map<UserAttribute, AttributeType> getUpdatedAttributes() {
    return updatedAttributes;
  }

  private void addEmailAttributes(String newEmail) {

    if (newEmail == null) {
      return;
    }

    String existedEmail = getAttributeValue(existedCognitoUser, EMAIL.getName());

    if (!newEmail.equalsIgnoreCase(existedEmail)) {
      updatedAttributes.putAll(buildEmailAttributes(newEmail));
    }
  }

  private void addAttribute(UserAttribute userAttribute, String newAttrValue) {
    addAttribute(userAttribute, newAttrValue, stringAttributeHelper);
  }

  private void addDelimitedAttribute(UserAttribute userAttribute, Set<String> newValues) {
    addAttribute(userAttribute, newValues, collectionAttributeHelper);
  }

  private <T> void addAttribute(
      UserAttribute userAttribute, T newAttrValue, AttributeHelper<T> attributeHelper) {

    if (newAttrValue == null) {
      return;
    }

    T existedAttrValue = attributeHelper.getAttributeValue(existedCognitoUser, userAttribute);

    if (!newAttrValue.equals(existedAttrValue)) {
      updatedAttributes
          .put(userAttribute, attributeHelper.createAttribute(userAttribute, newAttrValue));
    }
  }
}
