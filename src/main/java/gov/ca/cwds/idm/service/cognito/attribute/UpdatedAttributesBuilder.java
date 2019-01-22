package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PHONE_EXTENSION;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.OtherUserAttribute.ACCOUNT_STATUS;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.idm.service.cognito.util.CognitoPhoneConverter.toCognitoFormat;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.cognito.attribute.diff.UserAttributeDiff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.builder.AccountStatusDiffBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.diff.builder.CollectionAttributeDiffBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.diff.builder.EmailAttributeDiffBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.diff.builder.RoleAttributeDiffBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.diff.builder.StringAttributeDiffBuilder;
import gov.ca.cwds.idm.service.cognito.attribute.diff.builder.UserAttributeDiffBuilder;
import gov.ca.cwds.util.Utils;
import java.util.HashMap;
import java.util.Map;

public class UpdatedAttributesBuilder {

  private final UserType existedCognitoUser;
  private final UserUpdate updateUserDto;
  private final Map<UserAttribute, UserAttributeDiff> updatedAttributes = new HashMap<>();

  public UpdatedAttributesBuilder(UserType existedCognitoUser, UserUpdate updateUserDto) {
    this.existedCognitoUser = existedCognitoUser;
    this.updateUserDto = updateUserDto;
  }

  private <T> void addDiff(UserAttribute userAttribute,
      UserAttributeDiffBuilder<T> diffBuilder) {
    if (diffBuilder.doesDiffExist()) {
      updatedAttributes.put(userAttribute, diffBuilder.buildDiff());
    }
  }

  public Map<UserAttribute, UserAttributeDiff> buildUpdatedAttributesMap() {
    addDiff(EMAIL, new EmailAttributeDiffBuilder(existedCognitoUser,
        Utils.toLowerCase(updateUserDto.getEmail())));
    addDiff(PHONE_NUMBER,
        new StringAttributeDiffBuilder(PHONE_NUMBER, existedCognitoUser,
            toCognitoFormat(updateUserDto.getPhoneNumber())));
    addDiff(PHONE_EXTENSION,
        new StringAttributeDiffBuilder(PHONE_EXTENSION, existedCognitoUser,
            updateUserDto.getPhoneExtensionNumber()));
    addDiff(PERMISSIONS,
        new CollectionAttributeDiffBuilder(PERMISSIONS, existedCognitoUser,
            updateUserDto.getPermissions()));
    addDiff(ROLES,
        new RoleAttributeDiffBuilder(existedCognitoUser,
            updateUserDto.getRoles()));
    addDiff(ACCOUNT_STATUS, new AccountStatusDiffBuilder(ACCOUNT_STATUS, existedCognitoUser,
        updateUserDto.getEnabled()));
    return updatedAttributes;
  }

}
