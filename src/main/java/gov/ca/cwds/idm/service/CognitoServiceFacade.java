package gov.ca.cwds.idm.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CognitoServiceFacade {
  @Value("${cognito.iamAccessKeyId}")
  private String accessKeyId;

  @Value("${cognito.iamSecretKey}")
  private String secretKey;

  @Value("${cognito.region}")
  private String region;

  @Value("${cognito.userpool}")
  private String userpool;

  private AWSCognitoIdentityProvider identityProvider;

  @PostConstruct
  public void init() {
    AWSCredentialsProvider credentialsProvider =
        new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretKey));
    identityProvider =
        AWSCognitoIdentityProviderClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion(Regions.fromName(region))
            .build();
  }

  public AdminGetUserResult getById(String id) {
    AdminGetUserRequest request =
        new AdminGetUserRequest().withUsername(id).withUserPoolId(userpool);
    return identityProvider.adminGetUser(request);
  }
}
