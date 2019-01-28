package gov.ca.cwds.idm.event;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.UserUpdateRequest;
import gov.ca.cwds.idm.service.cognito.attribute.diff.CollectionUserAttributeDiff;
import gov.ca.cwds.util.IdToNameConverter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Created by Alexander Serbin on 1/11/2019
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PermissionsChangedEvent extends UserAttributeChangedEvent {

  private static final long serialVersionUID = 5084376904261765730L;

  public static final String EVENT_TYPE_PERMISSIONS_CHANGED = "Permission Change";

  public PermissionsChangedEvent(UserUpdateRequest userUpdateRequest,
      List<Permission> permissions) {
    super(userUpdateRequest);
    setEventType(EVENT_TYPE_PERMISSIONS_CHANGED);
    Validate.isTrue(userUpdateRequest.isAttributeChanged(PERMISSIONS));
    CollectionUserAttributeDiff diff = (CollectionUserAttributeDiff) userUpdateRequest.getDiffMap()
        .get(PERMISSIONS);
    Map<String, String> permissionsHash = permissions.stream()
        .collect(Collectors.toMap(Permission::getName, Permission::getDescription));
    IdToNameConverter idToNameConverter = new IdToNameConverter(permissionsHash);
    setOldValue(StringUtils.join(idToNameConverter.getNamesByIds(diff.getOldValue()), ", "));
    setNewValue(StringUtils.join(idToNameConverter.getNamesByIds(diff.getNewValue()), ", "));
  }

}
