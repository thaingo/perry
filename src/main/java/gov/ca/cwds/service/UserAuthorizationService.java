package gov.ca.cwds.service;

import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import java.io.Serializable;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business layer object to work on {@link UserAuthorization}
 *
 * @author CWDS API Team
 */
@Service
public class UserAuthorizationService {
  @Autowired private CwsUserInfoService cwsUserInfoService;

  public UserAuthorization find(Serializable primaryKey) {
    return Optional.ofNullable(cwsUserInfoService.composeForUserAuthorization(primaryKey))
        .map(
            e ->
                new UserAuthorization(
                    e.getRacfId(),
                    e.isSocialWorker(),
                    false,
                    true,
                    e.getUserAuthPrivs(),
                    e.getStaffUnitAuths(),
                    e.getCwsOffice(),
                    e.getStaffPerson()))
        .orElse(null);
  }

  public void setCwsUserInfoService(CwsUserInfoService cwsUserInfoService) {
    this.cwsUserInfoService = cwsUserInfoService;
  }
}
