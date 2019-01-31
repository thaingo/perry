package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.DatabaseUserAttribute.NOTES;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.cognito.attribute.diff.Diff;
import gov.ca.cwds.idm.service.cognito.attribute.diff.StringDiff;
import java.util.HashMap;
import java.util.Map;

public class DatabaseDiffMapBuilder {

  private final User existedUser;
  private final UserUpdate updateUserDto;
  private final Map<UserAttribute, Diff> diffMap = new HashMap<>();

  public DatabaseDiffMapBuilder(User existedUser, UserUpdate updateUserDto) {
    this.existedUser = existedUser;
    this.updateUserDto = updateUserDto;
  }

  public Map<UserAttribute, Diff> build() {
    addStringDiff(NOTES, existedUser.getNotes(), updateUserDto.getNotes());
    return diffMap;
  }

  private void addStringDiff(UserAttribute userAttribute, String oldValue, String newValue) {
    if (areNotEqual(oldValue, newValue)) {
      diffMap.put(userAttribute, new StringDiff(oldValue, newValue));
    }
  }

  private <T> boolean areNotEqual(T oldValue, T newValue) {
    return newValue != null && !newValue.equals(oldValue);
  }
}
