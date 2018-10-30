package gov.ca.cwds.idm.service;

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

    ListOfValues usersPossibleRoles = new ListOfValues();
    usersPossibleRoles.setEditable(authorizationService.canEditRoles(user));
    usersPossibleRoles.setPossibleValues(adminRoleImplementorFactory.getPossibleUserRoles());

    editDetails.setRoles(usersPossibleRoles);

    return editDetails;
  }
}
