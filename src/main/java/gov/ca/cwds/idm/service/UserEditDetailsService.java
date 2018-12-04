package gov.ca.cwds.idm.service;

import static gov.ca.cwds.util.Utils.isRacfidUser;

import gov.ca.cwds.idm.dto.ListOfValues;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserEditDetails;
import gov.ca.cwds.idm.service.authorization.AuthorizationService;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class UserEditDetailsService {

  @Autowired
  private AuthorizationService authorizationService;

  @Autowired
  private AdminRoleImplementorFactory adminRoleImplementorFactory;

  public UserEditDetails getEditDetails(User user) {
    UserEditDetails editDetails = new UserEditDetails();

    editDetails.setEditable(authorizationService.canUpdateUser(user.getId()));
    editDetails.setRoles(getRoles(user));
    editDetails.setPermissions(getPermissions(user));

    return editDetails;
  }

  private ListOfValues getRoles(User user) {
    ListOfValues usersPossibleRoles = new ListOfValues();
    usersPossibleRoles.setEditable(authorizationService.canEditRoles(user));
    usersPossibleRoles.setPossibleValues(adminRoleImplementorFactory.getPossibleUserRoles());
    return usersPossibleRoles;
  }

  private ListOfValues getPermissions(User user) {
    ListOfValues usersPossiblePermissions = new ListOfValues();
    usersPossiblePermissions.setEditable(authorizationService.canEditPermissions(user));

    usersPossiblePermissions
        .setPossibleValues(adminRoleImplementorFactory.getPossibleUserPermissions(isRacfidUser(user)));

    return usersPossiblePermissions;
  }
}
