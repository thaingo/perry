package gov.ca.cwds.idm.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.diff.StringSetDiff;
import gov.ca.cwds.util.IdToNameConverter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PermissionsChangedEvent extends UserChangeLogEvent {

  private static final long serialVersionUID = 5084376904261765730L;

  public static final String EVENT_TYPE_PERMISSIONS_CHANGED = "Permission Change";

  public PermissionsChangedEvent(User existedUser, StringSetDiff permissionsDiff, List<Permission> permissions) {
    super(EVENT_TYPE_PERMISSIONS_CHANGED, existedUser);

    Map<String, String> permissionsHash = permissions.stream()
        .collect(Collectors.toMap(Permission::getName, Permission::getDescription));
    IdToNameConverter idToNameConverter = new IdToNameConverter(permissionsHash);
    setOldValue(StringUtils.join(idToNameConverter.getNamesByIds(permissionsDiff.getOldValue()), ", "));
    setNewValue(StringUtils.join(idToNameConverter.getNamesByIds(permissionsDiff.getNewValue()), ", "));
  }
}
