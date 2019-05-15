package gov.ca.cwds.idm.service;

import static gov.ca.cwds.util.Utils.isRacfidUser;

import gov.ca.cwds.idm.dto.ListOfValues;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserEditDetails;
import gov.ca.cwds.idm.service.authorization.AuthorizationService;
import gov.ca.cwds.idm.service.filter.MainRoleFilter;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class UserEditDetailsService {

  private AuthorizationService authorizationService;

  private AdminRoleImplementorFactory adminRoleImplementorFactory;

  private PossibleUserPermissionsService possibleUserPermissionsService;

  public UserEditDetails getEditDetails(User user) {
    UserEditDetails editDetails = new UserEditDetails();

    boolean canUpdateUser = authorizationService.canUpdateUser(user);

    editDetails.setEditable(canUpdateUser);
    editDetails.setRoles(getRoles(user, canUpdateUser));
    editDetails.setPermissions(getPermissions(user, canUpdateUser));

    return editDetails;
  }

  private ListOfValues getRoles(User user, boolean canUpdateUser) {
    ListOfValues usersPossibleRoles = new ListOfValues();
    usersPossibleRoles.setEditable(canUpdateUser && authorizationService.canEditRoles(user));
    List<String> possibleValues = adminRoleImplementorFactory.getPossibleUserRoles();
    Set<String> extendedPossibleValues = new HashSet<>(possibleValues);
    Optional.of(MainRoleFilter.getMainRole(user)).ifPresent(extendedPossibleValues::add);
    usersPossibleRoles.setPossibleValues(new ArrayList<>(extendedPossibleValues));
    return usersPossibleRoles;
  }

  private ListOfValues getPermissions(User user, boolean canUpdateUser) {
    ListOfValues usersPossiblePermissions = new ListOfValues();
    usersPossiblePermissions
        .setEditable(canUpdateUser && authorizationService.canEditPermissions(user));

    usersPossiblePermissions.setPossibleValues(
        possibleUserPermissionsService.getPossibleUserPermissions(isRacfidUser(user)));

    return usersPossiblePermissions;
  }

  @Autowired
  public void setAuthorizationService(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  @Autowired
  public void setAdminRoleImplementorFactory(
      AdminRoleImplementorFactory adminRoleImplementorFactory) {
    this.adminRoleImplementorFactory = adminRoleImplementorFactory;
  }

  @Autowired
  public void setPossibleUserPermissionsService(
      PossibleUserPermissionsService possibleUserPermissionsService) {
    this.possibleUserPermissionsService = possibleUserPermissionsService;
  }
}
