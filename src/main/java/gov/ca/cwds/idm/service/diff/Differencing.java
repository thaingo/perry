package gov.ca.cwds.idm.service.diff;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class Differencing {

  private final StringDiff emailDiff;
  private final BooleanDiff enabledDiff;
  private final StringDiff phoneNumberDiff;
  private final StringDiff phoneExtensionNumberDiff;
  private final StringDiff notesDiff;
  private final StringSetDiff permissionsDiff;
  private final StringSetDiff rolesDiff;

  public Differencing(final User existedUser, final UserUpdate userUpdate) {
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

  private StringDiff createStringDiff(String oldValue, String newValue) {
    return createDiff(oldValue, newValue, StringDiff::new);
  }

  private BooleanDiff createBooleanDiff(Boolean oldValue, Boolean newValue) {
    return createDiff(oldValue, newValue, BooleanDiff::new);
  }

  private StringSetDiff createStringSetDiff(Set<String> oldValue, Set<String> newValue) {
    return createDiff(oldValue, newValue, StringSetDiff::new);
  }

  private <T, R> R createDiff(T oldValue, T newValue, BiFunction<T, T, R> diffConstructor) {
    if (areNotEqual(oldValue, newValue)) {
      return diffConstructor.apply(oldValue, newValue);
    } else {
      return null;
    }
  }

  private <T> boolean areNotEqual(T oldValue, T newValue) {
    return newValue != null && !newValue.equals(oldValue);
  }

  public Optional<StringDiff> getEmailDiff() {
    return Optional.ofNullable(emailDiff);
  }

  public Optional<BooleanDiff> getEnabledDiff() {
    return Optional.ofNullable(enabledDiff);
  }

  public Optional<StringDiff> getPhoneNumberDiff() {
    return Optional.ofNullable(phoneNumberDiff);
  }

  public Optional<StringDiff> getPhoneExtensionNumberDiff() {
    return Optional.ofNullable(phoneExtensionNumberDiff);
  }

  public Optional<StringDiff> getNotesDiff() {
    return Optional.ofNullable(notesDiff);
  }

  public Optional<StringSetDiff> getPermissionsDiff() {
    return Optional.ofNullable(permissionsDiff);
  }

  public Optional<StringSetDiff> getRolesDiff() {
    return Optional.ofNullable(rolesDiff);
  }
}
