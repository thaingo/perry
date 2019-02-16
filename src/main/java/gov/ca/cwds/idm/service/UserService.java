package gov.ca.cwds.idm.service;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("idm")
public class UserService {

  @Autowired
  private MappingService mappingService;

  @Autowired
  private CognitoServiceFacade cognitoServiceFacade;

  public User getUser(String id) {
    UserType cognitoUser = cognitoServiceFacade.getCognitoUserById(id);
    return mappingService.toUser(cognitoUser);
  }

}
