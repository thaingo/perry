package gov.ca.cwds.idm.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.CognitoProperties;
import gov.ca.cwds.rest.api.domain.PerryException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;

@Service
@Profile("idm")
public class CognitoServiceFacade {
  @Autowired private CognitoProperties properties;

  private AWSCognitoIdentityProvider identityProvider;

  @PostConstruct
  public void init() {
    AWSCredentialsProvider credentialsProvider =
        new AWSStaticCredentialsProvider(
            new BasicAWSCredentials(properties.getIamAccessKeyId(), properties.getIamSecretKey()));
    identityProvider =
        AWSCognitoIdentityProviderClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion(Regions.fromName(properties.getRegion()))
            .build();
  }

  public UserType getById(String id) {
    try {
      AdminGetUserRequest request =
          new AdminGetUserRequest().withUsername(id).withUserPoolId(properties.getUserpool());
      AdminGetUserResult agur = identityProvider.adminGetUser(request);
      return new UserType()
          .withUsername(agur.getUsername())
          .withAttributes(agur.getUserAttributes())
          .withEnabled(agur.getEnabled())
          .withUserCreateDate(agur.getUserCreateDate())
          .withUserLastModifiedDate(agur.getUserLastModifiedDate())
          .withUserStatus(agur.getUserStatus());
    } catch (Exception e) {
      throw new PerryException("Exception while connecting to AWS Cognito", e);
    }
  }

  public Collection<UserType> getByCounty(String countyName, int pageSize) {
    if (StringUtils.isEmpty(countyName)) {
      return Collections.emptyList();
    }
    ListUsersRequest request =
        new ListUsersRequest()
            .withUserPoolId(properties.getUserpool())
            .withFilter("preferred_username ^= \"" + countyName + "\"")
            .withLimit(pageSize);
    try {
      ListUsersResult result = identityProvider.listUsers(request);
      return result.getUsers();
    } catch (Exception e) {
      throw new PerryException("Exception while connecting to AWS Cognito", e);
    }
  }
}
