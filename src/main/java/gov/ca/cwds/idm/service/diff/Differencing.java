package gov.ca.cwds.idm.service.diff;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class Differencing {

  private Optional<StringDiff> emailDiff;
  private Optional<BooleanDiff> enabledDiff;
  private Optional<StringDiff> phoneNumberDiff;
  private Optional<StringDiff> phoneExtensionNumberDiff;
  private Optional<StringDiff> notesDiff;
  private Optional<StringSetDiff> permissionsDiff;
  private Optional<StringSetDiff> rolesDiff;

  public Differencing(User existedUser, UserUpdate userUpdate) {
    emailDiff = createStringDiff(existedUser.getEmail(), userUpdate.getEmail());
    enabledDiff = createBooleanDiff(existedUser.getEnabled(), userUpdate.getEnabled());
    phoneNumberDiff = createStringDiff(existedUser.getPhoneNumber(), userUpdate.getPhoneNumber());
    phoneExtensionNumberDiff =
        createStringDiff(existedUser.getPhoneExtensionNumber(),
            userUpdate.getPhoneExtensionNumber());
    notesDiff = createStringDiff(existedUser.getNotes(), userUpdate.getNotes());
    permissionsDiff = createStringSetDiff(existedUser.getPermissions(),
        userUpdate.getPermissions());
    rolesDiff = createStringSetDiff(existedUser.getRoles(), userUpdate.getRoles());
  }

  private Optional<StringDiff> createStringDiff(String oldValue, String newValue) {
    return createDiff(oldValue, newValue, StringDiff::new);
  }

  private Optional<BooleanDiff> createBooleanDiff(Boolean oldValue, Boolean newValue) {
    return createDiff(oldValue, newValue, BooleanDiff::new);
  }

  private Optional<StringSetDiff> createStringSetDiff(Set<String> oldValue, Set<String> newValue) {
    return createDiff(oldValue, newValue, StringSetDiff::new);
  }

  private <T, R> Optional<R> createDiff(T oldValue, T newValue,
      BiFunction<T, T, R> diffConstructor) {
    if (areNotEqual(oldValue, newValue)) {
      return Optional.of(diffConstructor.apply(oldValue, newValue));
    } else {
      return Optional.empty();
    }
  }

  private <T> boolean areNotEqual(T oldValue, T newValue) {
    return newValue != null && !newValue.equals(oldValue);
  }

  public Optional<StringDiff> getEmailDiff() {
    return emailDiff;
  }

  public Optional<BooleanDiff> getEnabledDiff() {
    return enabledDiff;
  }

  public Optional<StringDiff> getPhoneNumberDiff() {
    return phoneNumberDiff;
  }

  public Optional<StringDiff> getPhoneExtensionNumberDiff() {
    return phoneExtensionNumberDiff;
  }

  public Optional<StringDiff> getNotesDiff() {
    return notesDiff;
  }

  public Optional<StringSetDiff> getPermissionsDiff() {
    return permissionsDiff;
  }

  public Optional<StringSetDiff> getRolesDiff() {
    return rolesDiff;
  }
}
