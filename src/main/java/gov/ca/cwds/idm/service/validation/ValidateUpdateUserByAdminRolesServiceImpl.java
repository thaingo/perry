package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.service.messages.MessageCode.UNABLE_TO_REMOVE_ALL_ROLES;
import static gov.ca.cwds.service.messages.MessageCode.UNABLE_UPDATE_UNALLOWED_ROLES;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.role.implementor.AdminRoleImplementorFactory;
import gov.ca.cwds.rest.api.domain.UserIdmValidationException;
import gov.ca.cwds.service.messages.MessageCode;
import gov.ca.cwds.service.messages.MessagesService;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class ValidateUpdateUserByAdminRolesServiceImpl implements
    ValidateUpdateUserByAdminRolesService {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ValidateUpdateUserByAdminRolesServiceImpl.class);

  private AdminRoleImplementorFactory adminRoleImplementorFactory;

  private MessagesService messagesService;

  @Override
  public void validateUpdateUser(User newUser) {
    validateByNewUserRoles(newUser);
  }

  private void validateByNewUserRoles(User newUser) {
    Collection<String> newUserRoles = newUser.getRoles();

    if (newUserRoles.isEmpty()) {
      throwValidationException(UNABLE_TO_REMOVE_ALL_ROLES);
    }

    Collection<String> allowedRoles = getAllowedRoles();
    if (!allowedRoles.containsAll(newUserRoles)) {
      throwValidationException(UNABLE_UPDATE_UNALLOWED_ROLES, newUserRoles, allowedRoles);
    }
  }

  private void throwValidationException(MessageCode messageCode, Object... args) {
    String msg = messagesService.getTechMessage(messageCode, args);
    String userMsg = messagesService.getUserMessage(messageCode, args);
    LOGGER.error(msg);
    throw new UserIdmValidationException(msg, userMsg, messageCode);
  }

  private Collection<String> getAllowedRoles() {
    return adminRoleImplementorFactory.getPossibleUserRoles();
  }

  @Autowired
  public void setAdminRoleImplementorFactory(
      AdminRoleImplementorFactory adminRoleImplementorFactory) {
    this.adminRoleImplementorFactory = adminRoleImplementorFactory;
  }

  @Autowired
  public void setMessagesService(MessagesService messagesService) {
    this.messagesService = messagesService;
  }
}
