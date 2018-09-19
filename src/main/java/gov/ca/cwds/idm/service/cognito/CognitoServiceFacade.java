package gov.ca.cwds.idm.service.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserEnableStatusRequest;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUserPage;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import gov.ca.cwds.service.messages.MessagesService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;

/**
 * Created by Alexander Serbin on 9/13/2018
 */
@Profile("idm")
public interface CognitoServiceFacade {

  UserType createUser(User user);

  AdminCreateUserRequest createAdminCreateUserRequest(User user);

  CognitoUserPage searchPage(CognitoUsersSearchCriteria searchCriteria);

  List<UserType> searchAllPages(CognitoUsersSearchCriteria searchCriteria);

  UserType getCognitoUserById(String id);

  AdminGetUserRequest createAdminGetUserRequest(String id);

  /**
   * Returns last authenticated timestamp as last out of all authenticated timestamps from different
   * login devices
   *
   * @see AWSCognitoIdentityProvider#adminListDevices
   */
  Optional<LocalDateTime> getLastAuthenticatedTimestamp(String userId);

  //method is used in annotation, don't remove it
  String getCountyName(String userId);

  void healthCheck();

  /**
   @return true if Cognito operations were really executed, false otherwise
   */
  boolean updateUserAttributes(
      String id, UserType existedCognitoUser, UserUpdate updateUserDto);

  /**
   @return true if Cognito operations were really executed, false otherwise
   */
  boolean changeUserEnabledStatus(UserEnableStatusRequest request);

  void setMessagesService(MessagesService messages);

  void setProperties(CognitoProperties properties);

}
