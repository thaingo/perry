package gov.ca.cwds.idm;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupResult;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IdmService {

  public List<User> getUsers() {
    AWSCognitoIdentityProviderClient identityUserPoolProviderClient;

    // omitted stuff...
    // initialize the Cognito Provider client.  This is used to talk to the user pool
    identityUserPoolProviderClient = new AWSCognitoIdentityProviderClient(new BasicAWSCredentials("AKIAJC2X3HJISHKSBITA", "2w7Ik6KGQtOHy/BqQPF07FFF6tmECKdCS/Ur153g")); // creds are loaded via variables that are supplied to my program dynamically
    identityUserPoolProviderClient.setRegion(RegionUtils.getRegion("us-east-2")); // var loaded


    // ...some code omitted
    ListUsersRequest listUsersRequest = new ListUsersRequest();
    listUsersRequest.withUserPoolId("us-east-2_sQK657Nsc"); // id of the userpool, look this up in Cognito console
    //listUsersRequest.withFilter("sub=xyz");  // i THINK this is how the Filter works... the documentation is terribad
    //listUsersRequest.withFilter("username = \"testFromCode\"");


    // get the results
    //ListUsersResult result = identityUserPoolProviderClient.listUsers(listUsersRequest);

    ListUsersInGroupRequest listUsersInGroupRequest = new ListUsersInGroupRequest();
    listUsersInGroupRequest.withGroupName("SuperCounty").withUserPoolId("us-east-2_sQK657Nsc");
    ListUsersInGroupResult result = identityUserPoolProviderClient.listUsersInGroup(listUsersInGroupRequest);

    List<UserType> userTypeList = result.getUsers();
    List<User> resultList = new ArrayList<>(userTypeList.size());
    // loop through them
    for (UserType userType : userTypeList) {
      resultList.add(convert(userType, "SuperCounty"));


    }
    return resultList;
  }


  private User convert(UserType userType, String countyName) {
    return new User(userType.getUsername(), countyName, convertAttributest(userType.getAttributes()));
  }

  private List<String> convertAttributest(List<AttributeType> attributes) {
    return attributes.stream().filter(attributeType -> !attributeType.getName().equals("sub")).map(this::getAsString).collect(Collectors.toList());
  }

  private String getAsString(AttributeType attributeType) {
    return attributeType.getName()+ ": " + attributeType.getValue() + " ";
  }

  public List getRoles() {
    return Collections.emptyList();
  }
}
