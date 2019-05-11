package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.idm.service.filter.MainRoleFilter.getMainRole;
import static gov.ca.cwds.service.messages.MessageCode.ADMIN_CANNOT_UPDATE_HIMSELF;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUserName;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.exception.AdminAuthorizationException;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.role.implementor.AbstractAdminActionsAuthorizer;
import gov.ca.cwds.idm.service.role.implementor.AdminActionsAuthorizerFactory;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("authorizationService")
@Profile("idm")
public class AuthorizationServiceImpl implements AuthorizationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

  private AdminActionsAuthorizerFactory adminRoleImplementorFactory;

  private ExceptionFactory exceptionFactory;

  @Override
  public void checkCanViewUser(User user) {
    getAdminActionsAuthorizer(user).checkCanViewUser();
  }

  @Override
  public void checkCanCreateUser(User user) {
    getAdminActionsAuthorizer(user).checkCanCreateUser();
  }

  @Override
  public void checkCanUpdateUser(User user, UserUpdate updateUserDto) {
    if (user.getId().equals(getCurrentUserName())) {
      throw exceptionFactory.createAuthorizationException(ADMIN_CANNOT_UPDATE_HIMSELF);
    }
    getAdminActionsAuthorizer(user).checkCanUpdateUser(updateUserDto);
  }

  @Override
  @SuppressWarnings({"squid:S1166", "fb-contrib:EXS_EXCEPTION_SOFTENING_RETURN_FALSE"})
  //squid:S1166: exceptions stack trace can be omitted in this context
  //fb-contrib:EXS_EXCEPTION_SOFTENING_RETURN_FALSE: our design needs a boolean result
  public boolean canUpdateUser(User user, UserUpdate updateUser) {
    try {
      checkCanUpdateUser(user, updateUser);
    } catch (AdminAuthorizationException e) {
      LOGGER.info("user update can not be authorized, since: {}",  e.getUserMessage());
      return false;
    }
    return true;
  }

  @Override
  public List<String> getRolesListForUI(User existedUser) {
    if(canUpdateUser(existedUser, new UserUpdate())) {
      return getAdminActionsAuthorizer(existedUser).getMaxAllowedUserRolesAtUpdate();
    } else {
      return Collections.singletonList(getMainRole(existedUser.getRoles()));
    }
  }

  private AdminActionsAuthorizer getAdminActionsAuthorizer(User user) {
    AbstractAdminActionsAuthorizer authorizer = adminRoleImplementorFactory
        .getAdminActionsAuthorizer(user);
    authorizer.setExceptionFactory(exceptionFactory);
    return authorizer;
  }

  @Override
  public void checkCanResendInvitationMessage(User user) {
    getAdminActionsAuthorizer(user).checkCanResendInvitationMessage();
  }

  @Override
  public void checkCanUnlockUser(User user) {
    getAdminActionsAuthorizer(user).checkCanUpdateUser(new UserUpdate());
  }

  @Autowired
  public void setAdminRoleImplementorFactory(
      AdminActionsAuthorizerFactory adminRoleImplementorFactory) {
    this.adminRoleImplementorFactory = adminRoleImplementorFactory;
  }

  @Autowired
  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
