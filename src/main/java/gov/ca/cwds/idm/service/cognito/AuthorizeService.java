package gov.ca.cwds.idm.service.cognito;

import gov.ca.cwds.idm.dto.User;

/**
 * Created by Alexander Serbin on 9/26/2018
 */
public interface AuthorizeService {

  boolean canFindUser(User user);

  boolean canCreateUser(User user);

  boolean canUpdateUser(String userId);

  boolean canResendInvitationMessage(String userId);

}
