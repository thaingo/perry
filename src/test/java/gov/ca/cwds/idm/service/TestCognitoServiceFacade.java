package gov.ca.cwds.idm.service;

import static org.mockito.Mockito.mock;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import gov.ca.cwds.idm.CognitoProperties;
import javax.annotation.PostConstruct;

public class TestCognitoServiceFacade extends CognitoServiceFacade {

  @PostConstruct
  @Override
  public void init() {
    AWSCognitoIdentityProvider identityProvider = mock(AWSCognitoIdentityProvider.class);

    CognitoProperties properties = new CognitoProperties();
    properties.setIamAccessKeyId("iamAccessKeyId");
    properties.setIamSecretKey("iamSecretKey");
    properties.setUserpool("userpool");
    properties.setRegion("us-east-2");

    setProperties(properties);
    setIdentityProvider(identityProvider);
  }
}
