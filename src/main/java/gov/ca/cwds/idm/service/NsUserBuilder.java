package gov.ca.cwds.idm.service;

import static gov.ca.cwds.util.Utils.blankToNull;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.diff.Diff;
import gov.ca.cwds.idm.service.diff.StringDiff;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
import java.util.Optional;
import java.util.function.Consumer;

public class NsUserBuilder {
  private final NsUser nsUser;
  private final UpdateDifference updateDifference;
  private boolean userIsUpdated = false;

  public NsUserBuilder(NsUser nsUser, UpdateDifference updateDifference) {
    this.nsUser = nsUser;
    this.updateDifference = updateDifference;
  }

  public boolean userIsUpdated() {
    return userIsUpdated;
  }

  public NsUser build() {
    setProperty(updateDifference.getPhoneNumberDiff(), nsUser::setPhoneNumber);
    setNullableStringProperty(updateDifference.getPhoneExtensionNumberDiff(), nsUser::setPhoneExtensionNumber);
    setProperty(updateDifference.getNotesDiff(), nsUser::setNotes);
    setProperty(updateDifference.getRolesDiff(), nsUser::setRoles);
    setProperty(updateDifference.getPermissionsDiff(), nsUser::setPermissions);
    return nsUser;
  }

  private <D extends Diff<T>, T> void setProperty(Optional<D> diff, Consumer<T> setter) {
    if(diff.isPresent()) {
      T newValue = diff.get().getNewValue();
      setter.accept(newValue);
      userIsUpdated = true;
    }
  }

  private  void setNullableStringProperty(Optional<StringDiff> diff, Consumer<String> setter) {
    if(diff.isPresent()) {
      String newValue = diff.get().getNewValue();
      setter.accept(blankToNull(newValue));
      userIsUpdated = true;
    }
  }
}
