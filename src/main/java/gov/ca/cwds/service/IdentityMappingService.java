package gov.ca.cwds.service;

import javax.script.ScriptException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import gov.ca.cwds.service.scripts.IdentityMappingScript;

/**
 * Created by dmitry.rudenko on 5/9/2017.
 */
@Service
public class IdentityMappingService {

  private static final String DEFAULT_SP_ID_NAME = "default";


  private UserAuthorizationService userAuthorizationService;

  private PerryProperties configuration;

  public String map(UniversalUserToken subject, String providerId) {
    IdentityMappingScript mappingScript = loadMappingScriptForServiceProvider(providerId);
    if (mappingScript != null) {
      UserAuthorization authorization = userAuthorizationService.find(subject.getUserId());
      subject.setAuthorization(authorization);
      try {
        return mappingScript.map(subject);
      } catch (ScriptException e) {
        throw new IllegalArgumentException(
            "Identity Mapping failed for service provider: " + providerId, e);
      }
    }
    return subject.getUserId();
  }

  private IdentityMappingScript loadMappingScriptForServiceProvider(String serviceProviderId) {
    return loadScript(
        StringUtils.isEmpty(serviceProviderId) ? DEFAULT_SP_ID_NAME : serviceProviderId);
  }

  private IdentityMappingScript loadScript(String serviceProviderId) {
    PerryProperties.ServiceProviderConfiguration spConfiguration =
        configuration.getServiceProviders().get(serviceProviderId);
    if (spConfiguration != null) {
      return spConfiguration.getIdentityMapping();
    }
    return null;
  }

  @Autowired
  public void setUserAuthorizationService(UserAuthorizationService userAuthorizationService) {
    this.userAuthorizationService = userAuthorizationService;
  }

  @Autowired
  public void setConfiguration(PerryProperties configuration) {
    this.configuration = configuration;
  }
}
