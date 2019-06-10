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
    emailDiff = createStringDiff(
        existedUser.getEmail(),
        Utils.toLowerCase(userUpdate.getEmail()));

    enabledDiff = createBooleanDiff(existedUser.getEnabled(), userUpdate.getEnabled());

    phoneNumberDiff = createStringDiff(existedUser.getPhoneNumber(), userUpdate.getPhoneNumber());

    phoneExtensionNumberDiff =
        createStringDiff(existedUser.getPhoneExtensionNumber(),
            userUpdate.getPhoneExtensionNumber());

    cellPhoneNumberDiff = createStringDiff(existedUser.getCellPhoneNumber(),
        userUpdate.getCellPhoneNumber());

    notesDiff = createStringDiff(existedUser.getNotes(), userUpdate.getNotes());

    permissionsDiff = createStringSetDiff(existedUser.getPermissions(),
        userUpdate.getPermissions());

    rolesDiff = createStringSetDiff(existedUser.getRoles(), userUpdate.getRoles());
  }

  private StringDiff createStringDiff(String oldValue, String newValue) {
    return createDiff(oldValue, newValue, Utils::blankToNull, StringDiff::new);
  }

  private BooleanDiff createBooleanDiff(Boolean oldValue, Boolean newValue) {
    return createDiff(oldValue, newValue, BooleanDiff::new);
  }

  private StringSetDiff createStringSetDiff(Set<String> oldValue, Set<String> newValue) {
    return createDiff(oldValue, newValue, StringSetDiff::new);
  }

  private <T, R> R createDiff(T oldValue, T newValue, Function<T, T> newValueNormalizer,
      BiFunction<T, T, R> diffConstructor
      ) {
    if (newValue == null) {
      return null;//absence of the field in the input JSON is a sign that field is not edited
    }

    T normalizedNewValue = newValueNormalizer.apply(newValue);

    if (!Objects.equals(oldValue, normalizedNewValue)) {
      return diffConstructor.apply(oldValue, normalizedNewValue);
    } else {
      return null;
    }
  }

  private <T, R> R createDiff(T oldValue, T newValue, BiFunction<T, T, R> diffConstructor) {
    return createDiff(oldValue, newValue, t -> t, diffConstructor);
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
}
