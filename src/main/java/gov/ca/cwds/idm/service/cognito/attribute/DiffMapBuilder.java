package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.DatabaseUserAttribute.NOTES;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.cognito.attribute.diff.Diff;
import java.util.HashMap;
import java.util.Map;

public class DiffMapBuilder {

  private final User existedUser;
  private final UserUpdate updateUserDto;
  private final Map<UserAttribute, Diff> diffMap = new HashMap<>();

  public DiffMapBuilder(User existedUser, UserUpdate updateUserDto) {
    this.existedUser = existedUser;
    this.updateUserDto = updateUserDto;
  }

  public Map<UserAttribute, Diff> build() {
    addDiff(NOTES, existedUser.getNotes(), updateUserDto.getNotes());
    return diffMap;
  }

  private <T> void addDiff(UserAttribute userAttribute, T oldValue, T newValue) {
    if (newValue != null && !newValue.equals(oldValue)) {
      diffMap.put(userAttribute, new Diff(oldValue, newValue));
    }
  }
}
