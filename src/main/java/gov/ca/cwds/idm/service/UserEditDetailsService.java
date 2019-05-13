package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.filter.MainRoleFilter.getMainRole;
import static gov.ca.cwds.util.Utils.isRacfidUser;
import static java.util.Collections.singletonList;

import gov.ca.cwds.idm.dto.ListOfValues;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserEditDetails;
import gov.ca.cwds.idm.service.authorization.AuthorizationService;
import gov.ca.cwds.idm.service.role.implementor.AdminActionsAuthorizerFactory;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class UserEditDetailsService {

  private AuthorizationService authorizationService;

  private PossibleUserPermissionsService possibleUserPermissionsService;

  public UserEditDetails getEditDetails(User user) {
    UserEditDetails editDetails = new UserEditDetails();

    boolean canUpdateUser = authorizationService.canUpdateUser(user);

    editDetails.setEditable(canUpdateUser);
    editDetails.setRoles(getRoles(user));
    editDetails.setPermissions(getPermissions(user, canUpdateUser));

    return editDetails;
  }

  private ListOfValues getRoles(User user) {
    ListOfValues usersPossibleRoles = new ListOfValues();

    List<String> possibleRoles = authorizationService.getAllowedUiRolesForUpdate(user);
    boolean rolesAreEditable =
        !CollectionUtils.subtract(possibleRoles, singletonList(getMainRole(user))).isEmpty();

    usersPossibleRoles.setEditable(rolesAreEditable);
    usersPossibleRoles.setPossibleValues(possibleRoles);
    return usersPossibleRoles;
  }

  private ListOfValues getPermissions(User user, boolean canUpdateUser) {
    ListOfValues usersPossiblePermissions = new ListOfValues();
    usersPossiblePermissions.setEditable(canUpdateUser);
    usersPossiblePermissions.setPossibleValues(
        possibleUserPermissionsService.getPossibleUserPermissions(isRacfidUser(user)));

    return usersPossiblePermissions;
  }

  @Autowired
  public void setAuthorizationService(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  @Autowired
  public void setPossibleUserPermissionsService(
      PossibleUserPermissionsService possibleUserPermissionsService) {
    this.possibleUserPermissionsService = possibleUserPermissionsService;
  }
}
