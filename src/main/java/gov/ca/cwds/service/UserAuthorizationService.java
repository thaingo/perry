package gov.ca.cwds.service;

import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import gov.ca.cwds.rest.services.CrudsService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Business layer object to work on {@link UserAuthorization}
 *
 * @author CWDS API Team
 */
@Service
public class UserAuthorizationService {
  @Autowired private CwsUserInfoService cwsUserInfoService;

  /**
   * {@inheritDoc}
   *
   * @see CrudsService#find(Serializable)
   */
  public UserAuthorization find(Serializable primaryKey) {
    CwsUserInfo userInfo = cwsUserInfoService.composeForUserAuthorization(primaryKey);

    return userInfo != null
        ? new UserAuthorization(
            userInfo.getUser().getLogonId(),
            userInfo.isSocialWorker(),
            false,
            true,
            userInfo.getUserAuthPrivs(),
            userInfo.getStaffUnitAuths(),
            userInfo.getCwsOffice(),
            userInfo.getStaffPerson())
        : null;
  }

  public void setCwsUserInfoService(CwsUserInfoService cwsUserInfoService) {
    this.cwsUserInfoService = cwsUserInfoService;
  }
}
