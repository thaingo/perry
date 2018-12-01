package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.ListOfValues;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserEditDetails;
import gov.ca.cwds.idm.persistence.ns.entity.Permission;
import gov.ca.cwds.idm.service.authorization.AuthorizationService;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import java.util.List;
import java.util.stream.Collectors;
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

  @Autowired
  private DictionaryProvider dictionaryProvider;

  public UserEditDetails getEditDetails(User user) {
    UserEditDetails editDetails = new UserEditDetails();

    editDetails.setEditable(authorizationService.canUpdateUser(user.getId()));
    editDetails.setRoles(getRoles(user));
    editDetails.setPermissions(getPermissions());

    return editDetails;
  }

  private ListOfValues getRoles(User user) {
    ListOfValues usersPossibleRoles = new ListOfValues();
    usersPossibleRoles.setEditable(authorizationService.canEditRoles(user));
    usersPossibleRoles.setPossibleValues(adminRoleImplementorFactory.getPossibleUserRoles());
    return usersPossibleRoles;
  }

  private ListOfValues getPermissions() {
    ListOfValues usersPossiblePermissions = new ListOfValues();
    usersPossiblePermissions.setEditable(true);

    List<String> permissionNames =
        dictionaryProvider.getPermissions().stream()
            .map(Permission::getName).collect(Collectors.toList());
    usersPossiblePermissions.setPossibleValues(permissionNames);

    return usersPossiblePermissions;
  }
}
