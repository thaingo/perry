package gov.ca.cwds.idm.service.validation;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.service.dto.CwsUserInfo;

public interface ValidationService {

  void validateUserCreate(UniversalUserToken admin, User user, CwsUserInfo cwsUser);

  void validateVerifyIfUserCanBeCreated(UniversalUserToken admin, User user, CwsUserInfo cwsUser);

  void validateUpdateUser(UniversalUserToken admin, UserType existedCognitoUser, UserUpdate updateUserDto);
}
