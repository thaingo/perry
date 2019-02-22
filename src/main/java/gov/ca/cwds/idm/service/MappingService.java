package gov.ca.cwds.idm.service;

import static gov.ca.cwds.service.messages.MessageCode.IDM_MAPPING_SCRIPT_ERROR;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.filter.MainRoleFilter;
import gov.ca.cwds.service.dto.CwsUserInfo;
import java.util.Set;
import javax.script.ScriptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
@SuppressWarnings({"fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS"})
public class MappingService {

  private PerryProperties configuration;

  private ExceptionFactory exceptionFactory;

  public User toUser(UserType cognitoUser, CwsUserInfo cwsUser, NsUser nsUser) {
    User user;
    try {
      user = configuration.getIdentityManager().getIdmMapping().map(cognitoUser, cwsUser, nsUser);
    } catch (ScriptException e) {
      throw exceptionFactory.createIdmException(IDM_MAPPING_SCRIPT_ERROR, e);
    }
    filterMainRole(user);
    return user;
  }

  private void filterMainRole(User user) {
    Set<String> roles = user.getRoles();
    if (!roles.isEmpty()) {
      user.setRoles(MainRoleFilter.filter(roles));
    }
  }

  @Autowired
  public void setConfiguration(PerryProperties configuration) {
    this.configuration = configuration;
  }

  @Autowired
  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
