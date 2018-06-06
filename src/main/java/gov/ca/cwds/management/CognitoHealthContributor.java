package gov.ca.cwds.management;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import gov.ca.cwds.idm.service.CognitoServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("idm")
@Component(value = "cognitoHealthCheck")
public class CognitoHealthContributor implements HealthIndicator {

  @Autowired private CognitoServiceFacade cognitoServiceFacade;

  @Override
  public Health health() {
    try {
      cognitoServiceFacade.healthCheck();
      return Health.up().status("Cognito Connection OK").build();
    } catch (AWSCognitoIdentityProviderException awsException) {
      return Health.down().withException(awsException).build();
    }
  }
}
