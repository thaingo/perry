package gov.ca.cwds.idm.service;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.idm.dto.UpdateUserDto;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.util.UsersSearchParametersUtil;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import gov.ca.cwds.service.UserAuthorizationService;
import gov.ca.cwds.service.scripts.IdmMappingScript;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.script.ScriptException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class CognitoIdmService implements IdmService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CognitoIdmService.class);
  static final String RACFID_ATTRIBUTE = "CUSTOM:RACFID";

  @Autowired CognitoServiceFacade cognitoService;

  @Autowired UserAuthorizationService userAuthorizationService;

  @Autowired private PerryProperties configuration;

  @Override
  @PostFilter("filterObject.countyName == principal.getParameter('county_name')")
  public List<User> getUsers(String lastName) {
    Collection<UserType> cognitoUsers =
        cognitoService.search(UsersSearchParametersUtil.composeSearchParameter(lastName));

    Map<String, String> userNameToRacfId = new HashMap<>(cognitoUsers.size());
    for (UserType user : cognitoUsers) {
      userNameToRacfId.put(user.getUsername(), getRACFId(user));
    }

    Map<String, UserAuthorization> idToCmsUser = userAuthorizationService.findUsers(userNameToRacfId.values())
            .stream().collect(Collectors.toMap(UserAuthorization::getUserId, e -> e,
                    (user1, user2) -> {
                      LOGGER.warn("UserAuthorization - duplicate UserId for RACFid: {}", user1.getUserId());
                      return user1;
                    }));

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
    return enrichCognitoUser(cognitoUser);
  }

  @Override
  @PreAuthorize("@cognitoServiceFacade.getCountyName(#id) == principal.getParameter('county_name')")
  public User updateUser(String id, UpdateUserDto updateUserDto) {
    UserType cognitoUser =  cognitoService.updateUser(id, updateUserDto);
    return enrichCognitoUser(cognitoUser);
  }

  private User enrichCognitoUser(UserType cognitoUser) {

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

  static String getRACFId(UserType user) {
    return CognitoUtils.getAttributeValue(user, RACFID_ATTRIBUTE);
  }
}
