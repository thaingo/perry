package gov.ca.cwds.idm.service;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.rest.api.domain.PerryException;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import gov.ca.cwds.service.UserAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@Profile("idm")
public class CognitoIdmService implements IdmService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CognitoIdmService.class);

  @Autowired CognitoServiceFacade cognitoService;

  @Autowired
  UserAuthorizationService userAuthorizationService;

  @Autowired private PerryProperties configuration;

  @Override
  public List<User> getUsers() {
    List<User> resultList = new ArrayList<>(20);
    for (int i = 0; i < 20; i++) {
      resultList.add(createUser(i));
    }
    return resultList;
  }

  @Override
  @PostAuthorize("returnObject.countyName == principal.getParameter('county_name')")
  public User findUser(String id) {
    AdminGetUserResult cognitoUser = cognitoService.getById(id);
    if (cognitoUser == null) {
      return null;
    }
    String racfId =
        cognitoUser
            .getUserAttributes()
            .stream()
            .filter(e -> e.getName().equals("custom:RACFID"))
            .findAny()
            .map(AttributeType::getValue)
            .orElse(null);

    UserAuthorization cwsUser = null;
    if (racfId != null) {
      cwsUser = userAuthorizationService.composeForIdm(racfId);
    }

    try {
      return configuration.getIdentityManager().getIdmMapping().map(cognitoUser, cwsUser);
    } catch (ScriptException e) {
      LOGGER.error("Error running the IdmMappingScript");
      throw new PerryException(e.getMessage(), e);
    }
  }

  // tmp mock
  private User createUser(int i) {
    User user = new User();

    user.setCountyName("MyCounty");
    user.setId("24051d54-9321-4dd2-a92f-6425d6c455be");
    user.setEnabled(i % 2 == 0);
    user.setEmail("email" + i + "@test.com");
    user.setOffice("Office " + i);
    user.setPhoneNumber("+1916999999" + i % 10);
    user.setPhoneExtensionNumber("" + i + i);
    user.setEndDate(new Date());
    user.setStartDate(new Date());
    user.setPermissions(new HashSet<>(Arrays.asList("Snapshot-rollout", "Hotline-rollout")));
    user.setFirstName("Firstname" + i);
    user.setLastName("Lastname" + i);
    user.setRacfid("RACFID" + i);
    user.setUserCreateDate(new Date());
    user.setUserLastModifiedDate(new Date());
    user.setStatus("userStatus" + i);
    user.setLastLoginDateTime(LocalDateTime.now().minusHours(i).plusMinutes(i).minusDays(i + 5L));
    return user;
  }
}
