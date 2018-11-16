package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.getRACFId;
import static gov.ca.cwds.service.messages.MessageCode.IDM_MAPPING_SCRIPT_ERROR;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.ns.entity.NsUser;
import gov.ca.cwds.idm.service.exception.ExceptionFactory;
import gov.ca.cwds.idm.service.filter.MainRoleFilter;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import java.util.Set;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
@SuppressWarnings({"fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS"})
public class MappingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MappingService.class);

  private PerryProperties configuration;

  private CwsUserInfoService cwsUserInfoService;

  private NsUserService nsUserService;

  private ExceptionFactory exceptionFactory;

  public User toUser(UserType cognitoUser) {
    String userId = cognitoUser.getUsername();
    String racfId = getRACFId(cognitoUser);

    CwsUserInfo cwsUser = cwsUserInfoService.getCwsUserByRacfId(racfId);
    NsUser nsUser = nsUserService.findByUsername(userId).orElse(null);

    return toUser(cognitoUser, cwsUser, nsUser);
  }

  public User toUser(UserType cognitoUser, CwsUserInfo cwsUser, NsUser nsUser) {
    User user;
    try {
      user =  configuration.getIdentityManager().getIdmMapping().map(cognitoUser, cwsUser);
    } catch (ScriptException e) {
      throw exceptionFactory.createIdmException(IDM_MAPPING_SCRIPT_ERROR);
    }
    enrichWithNsUser(user, nsUser);
    filterMainRole(user);
    return user;
  }

  public User toUserWithoutDbData(UserType cognitoUser) {
    return toUser(cognitoUser, null, null);
  }

  @Autowired
  public void setConfiguration(PerryProperties configuration) {
    this.configuration = configuration;
  }

  private void enrichWithNsUser(User user, NsUser nsUser) {
    if (nsUser == null) {
      return;
    }
    user.setLastLoginDateTime(nsUser.getLastLoginTime());
    user.setLastRegistrationResubmitDateTime(nsUser.getLastRegistrationResubmitTime());
  }

  private void filterMainRole(User user) {
    Set<String> roles = user.getRoles();
    if (!roles.isEmpty()) {
      user.setRoles(MainRoleFilter.filter(roles));
    }
  }

  @Autowired
  public void setCwsUserInfoService(CwsUserInfoService cwsUserInfoService) {
    this.cwsUserInfoService = cwsUserInfoService;
  }

  @Autowired
  public void setNsUserService(NsUserService nsUserService) {
    this.nsUserService = nsUserService;
  }

  @Autowired
  public void setExceptionFactory(ExceptionFactory exceptionFactory) {
    this.exceptionFactory = exceptionFactory;
  }
}
