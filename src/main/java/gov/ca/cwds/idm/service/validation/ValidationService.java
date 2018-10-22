package gov.ca.cwds.idm.service.validation;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.service.dto.CwsUserInfo;

public interface ValidationService {

  void validateUserCreate(User enrichedUser, CwsUserInfo cwsUser);

  void validateVerifyIfUserCanBeCreated(User enrichedUser, CwsUserInfo cwsUser);

  void validateUpdateUser(UserType existedCognitoUser, UserUpdate updateUserDto);
}
