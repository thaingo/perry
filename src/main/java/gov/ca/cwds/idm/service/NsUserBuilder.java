package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.diff.Diff;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
import java.util.Optional;
import java.util.function.Consumer;

public class NsUserBuilder {
  private final NsUser nsUser;
  private final UpdateDifference differencing;
  private boolean userIsUpdated = false;

  public NsUserBuilder(NsUser nsUser, UpdateDifference differencing) {
    this.nsUser = nsUser;
    this.differencing = differencing;
  }

  public boolean userIsUpdated() {
    return userIsUpdated;
  }

  public NsUser build() {
    setProperty(differencing.getNotesDiff(), nsUser::setNotes);
    return nsUser;
  }

  private <D extends Diff<T>, T> void setProperty(Optional<D> diff, Consumer<T> setter) {
    if(diff.isPresent()) {
      T newValue = diff.get().getNewValue();
      setter.accept(newValue);
      userIsUpdated = true;
    }
  }
}
