package gov.ca.cwds.idm.service.diff;

import static org.apache.commons.lang3.StringUtils.isBlank;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.util.Utils;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class UpdateDifference {

  private final StringDiff emailDiff;
  private final BooleanDiff enabledDiff;
  private final StringDiff phoneNumberDiff;
  private final StringDiff phoneExtensionNumberDiff;
  private final StringDiff cellPhoneNumberDiff;
  private final StringDiff notesDiff;
  private final StringSetDiff permissionsDiff;
  private final StringSetDiff rolesDiff;

  public UpdateDifference(final User existedUser, final UserUpdate userUpdate) {
    emailDiff = createStringDiff(userUpdate.isEmailUpdateRequested(),
        existedUser.getEmail(), Utils.toLowerCase(userUpdate.getEmail()));

    enabledDiff = createBooleanDiff(userUpdate.isEnabledUpdateRequested(),
        existedUser.getEnabled(), userUpdate.getEnabled());

    phoneNumberDiff = createStringDiff(userUpdate.isPhoneNumberUpdateRequested(),
        existedUser.getPhoneNumber(), userUpdate.getPhoneNumber());

    phoneExtensionNumberDiff = createStringDiff(userUpdate.isPhoneNumberUpdateRequested(),
            existedUser.getPhoneExtensionNumber(), userUpdate.getPhoneExtensionNumber());

    cellPhoneNumberDiff = createStringDiff(userUpdate.isCellPhoneNumberUpdateRequested(),
        existedUser.getCellPhoneNumber(), userUpdate.getCellPhoneNumber());

    notesDiff = createStringDiff(userUpdate.isNotesUpdateRequested(),
        existedUser.getNotes(), userUpdate.getNotes());

    permissionsDiff = createStringSetDiff(userUpdate.isPermissionsUpdateRequested(),
        existedUser.getPermissions(), userUpdate.getPermissions());

    rolesDiff = createStringSetDiff(userUpdate.isRolesUpdateRequested(),
        existedUser.getRoles(), userUpdate.getRoles());
  }

  private StringDiff createStringDiff(boolean updateRequested, String oldValue, String newValue) {
    return createDiff(updateRequested, oldValue, newValue, this::blankToNull, StringDiff::new);
  }

  private BooleanDiff createBooleanDiff(boolean updateRequested,
      Boolean oldValue, Boolean newValue) {
    return createDiff(updateRequested, oldValue, newValue, BooleanDiff::new);
  }

  private StringSetDiff createStringSetDiff(boolean updateRequested,
      Set<String> oldValue, Set<String> newValue) {
    return createDiff(updateRequested, oldValue, newValue, StringSetDiff::new);
  }

  private <T, R> R createDiff(boolean updateRequested, T oldValue, T newValue,
      Function<T, T> newValueNormalizer, BiFunction<T, T, R> diffConstructor) {

    if (!updateRequested) {
      return null;
    }

    T normalizedNewValue = newValueNormalizer.apply(newValue);

    if (!Objects.equals(oldValue, normalizedNewValue)) {
      return diffConstructor.apply(oldValue, normalizedNewValue);
    } else {
      return null;
    }
  }

  private <T, R> R createDiff(boolean updateRequested, T oldValue, T newValue,
      BiFunction<T, T, R> diffConstructor) {
    return createDiff(updateRequested, oldValue, newValue, t -> t, diffConstructor);
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

  public Optional<StringDiff> getCellPhoneNumberDiff() {
    return Optional.ofNullable(cellPhoneNumberDiff);
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

  private String blankToNull(String str) {
    return isBlank(str) ? null : str;
  }
}
