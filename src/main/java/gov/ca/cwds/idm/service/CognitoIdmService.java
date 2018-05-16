package gov.ca.cwds.idm.service;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.util.UsersSearchParametersUtil;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import gov.ca.cwds.service.UserAuthorizationService;
import gov.ca.cwds.service.scripts.IdmMappingScript;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Profile("idm")
public class CognitoIdmService implements IdmService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CognitoIdmService.class);
  private static final String RACFID_ATTRIBUTE = "CUSTOM:RACFID";

  @Autowired CognitoServiceFacade cognitoService;

  @Autowired UserAuthorizationService userAuthorizationService;

  @Autowired private PerryProperties configuration;

  @Override
  @PostFilter("filterObject.countyName == principal.getParameter('county_name')")
  public List<User> getUsers(String lastName) {
    Collection<UserType> cognitoUsers =
        cognitoService.search(UsersSearchParametersUtil.composeSearchParameter(lastName));

    Map<String, String> userNameToRacfId =
        cognitoUsers
            .stream()
            .collect(Collectors.toMap(UserType::getUsername, CognitoIdmService::getRACFId));

    Map<String, UserAuthorization> idToCmsUser =
        userAuthorizationService
            .findUsers(userNameToRacfId.values())
            .stream()
            .collect(Collectors.toMap(UserAuthorization::getUserId, e -> e));

    IdmMappingScript mapping = configuration.getIdentityManager().getIdmMapping();
    return cognitoUsers
        .stream()
        .map(
            e -> {
              try {
                return mapping.map(e, idToCmsUser.get(userNameToRacfId.get(e.getUsername())));
              } catch (ScriptException ex) {
                LOGGER.error("Error running the IdmMappingScript");
                throw new PerryException(ex.getMessage(), ex);
              }
            })
        .collect(Collectors.toList());
  }

  @Override
  @PostAuthorize("returnObject.countyName == principal.getParameter('county_name')")
  public User findUser(String id) {
    UserType cognitoUser = cognitoService.getById(id);
    if (cognitoUser == null) {
      return null;
    }
    String racfId = getRACFId(cognitoUser);

    UserAuthorization cwsUser = null;
    if (racfId != null) {
      List<UserAuthorization> users =
          userAuthorizationService.findUsers(Collections.singletonList(racfId));
      if (!CollectionUtils.isEmpty(users)) {
        cwsUser = users.get(0);
      }
    }

    try {
      return configuration.getIdentityManager().getIdmMapping().map(cognitoUser, cwsUser);
    } catch (ScriptException e) {
      LOGGER.error("Error running the IdmMappingScript");
      throw new PerryException(e.getMessage(), e);
    }
  }

  private static String getRACFId(UserType user) {
    return user.getAttributes()
        .stream()
        .filter(a -> a.getName().equalsIgnoreCase(RACFID_ATTRIBUTE))
        .findAny()
        .map(AttributeType::getValue)
        .orElse(null);
  }
}
