package gov.ca.cwds.idm.service.cognito;

import com.amazonaws.services.cognitoidp.model.AdminListDevicesResult;
import com.amazonaws.services.cognitoidp.model.DeviceType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

/**
 * Created by Alexander Serbin on 9/17/2018
 */
final class UserLastAuthenticatedTimestampExtractor {

  private UserLastAuthenticatedTimestampExtractor() {
  }

  static Optional<LocalDateTime> extractUserLastAuthenticatedTimestamp(
      AdminListDevicesResult adminListDevicesResult) {
    if (adminListDevicesResult.getDevices() == null) {
      return Optional.empty();
    }
    return adminListDevicesResult.getDevices().stream()
        .map(DeviceType::getDeviceLastAuthenticatedDate).min(Comparator.reverseOrder())
        .map(Date::toInstant)
        .map(instant -> instant.atZone(ZoneId.systemDefault())).map(
            ZonedDateTime::toLocalDateTime);
  }

}
