package gov.ca.cwds.idm.service.validation;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;

public interface ValidationService {

  User validateUserCreate(UniversalUserToken admin, User user);

  void validateUpdateUser(UniversalUserToken admin, UserType existedCognitoUser, UserUpdate updateUserDto);

}
