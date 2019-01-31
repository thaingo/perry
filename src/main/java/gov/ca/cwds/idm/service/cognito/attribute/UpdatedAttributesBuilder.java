package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PHONE_EXTENSION;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.OtherUserAttribute.ENABLED_STATUS;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.idm.service.cognito.util.CognitoPhoneConverter.toCognitoFormat;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.diff.UserAttributeDiff;
import gov.ca.cwds.idm.service.diff.builder.CollectionAttributeDiffBuilder;
import gov.ca.cwds.idm.service.diff.builder.EmailAttributeDiffBuilder;
import gov.ca.cwds.idm.service.diff.builder.RoleAttributeDiffBuilder;
import gov.ca.cwds.idm.service.diff.builder.StringAttributeDiffBuilder;
import gov.ca.cwds.idm.service.diff.builder.UserAttributeDiffBuilder;
import gov.ca.cwds.idm.service.diff.builder.UserEnabledStatusDiffBuilder;
import gov.ca.cwds.util.Utils;
import java.util.HashMap;
import java.util.Map;

public class UpdatedAttributesBuilder {

  private final User existedUser;
  private final UserUpdate updateUserDto;
  private final Map<UserAttribute, UserAttributeDiff> updatedAttributes = new HashMap<>();

  public UpdatedAttributesBuilder(User existedUser, UserUpdate updateUserDto) {
    this.existedUser = existedUser;
    this.updateUserDto = updateUserDto;
  }

  private <T> void addDiff(UserAttribute userAttribute,
      UserAttributeDiffBuilder<T> diffBuilder) {
    if (diffBuilder.doesDiffExist()) {
      updatedAttributes.put(userAttribute, diffBuilder.buildDiff());
    }
  }

  public Map<UserAttribute, UserAttributeDiff> buildUpdatedAttributesMap() {
    addDiff(EMAIL, new EmailAttributeDiffBuilder(
        Utils.toLowerCase(existedUser.getEmail()),
        Utils.toLowerCase(updateUserDto.getEmail())));
    addDiff(PHONE_NUMBER,
        new StringAttributeDiffBuilder(PHONE_NUMBER,
            toCognitoFormat(existedUser.getPhoneNumber()),
            toCognitoFormat(updateUserDto.getPhoneNumber())));
    addDiff(PHONE_EXTENSION,
        new StringAttributeDiffBuilder(PHONE_EXTENSION,
            existedUser.getPhoneExtensionNumber(),
            updateUserDto.getPhoneExtensionNumber()));
    addDiff(PERMISSIONS,
        new CollectionAttributeDiffBuilder(PERMISSIONS,
            existedUser.getPermissions(),
            updateUserDto.getPermissions()));
    addDiff(ROLES,
        new RoleAttributeDiffBuilder(existedUser.getRoles(), updateUserDto.getRoles()));
    addDiff(ENABLED_STATUS, new UserEnabledStatusDiffBuilder(ENABLED_STATUS,
        existedUser.getEnabled(),
        updateUserDto.getEnabled()));
    return updatedAttributes;
  }

}
