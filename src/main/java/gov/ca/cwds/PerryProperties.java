package gov.ca.cwds;

import gov.ca.cwds.security.jwt.JwtConfiguration;
import gov.ca.cwds.service.scripts.IdentityMappingScript;
import gov.ca.cwds.service.scripts.IdmMappingScript;
import gov.ca.cwds.service.scripts.IdpMappingScript;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Created by dmitry.rudenko on 5/22/2017.
 */
@ConfigurationProperties(prefix = "perry")
public class PerryProperties {

  @NestedConfigurationProperty
  private JwtConfiguration jwt;

  @NestedConfigurationProperty
  private IdentityProviderConfiguration identityProvider;

  @NestedConfigurationProperty
  private IdentityManagerConfiguration identityManager;

  private String users;

  private List<String> whiteList = new ArrayList<>();

  private Map<String, ServiceProviderConfiguration> serviceProviders = new HashMap<>();

  private String homePageUrl = "/";

  private boolean showErrors;

  private int tokenRecordTimeout;

  private int idpRetryTimeout;
  private int idpMaxAttempts;
  private int idpValidateInterval;

  public static class IdentityProviderConfiguration {

    private IdpMappingScript idpMapping;

    public IdpMappingScript getIdpMapping() {
      return idpMapping;
    }

    public void setIdpMapping(String idpMapping) throws IOException {
      this.idpMapping = new IdpMappingScript(idpMapping);
    }
  }

  public static class ServiceProviderConfiguration {

    private String id;
    private IdentityMappingScript identityMapping;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public IdentityMappingScript getIdentityMapping() {
      return identityMapping;
    }

    public void setIdentityMapping(String identityMapping) throws IOException {
      this.identityMapping = new IdentityMappingScript(identityMapping);
    }
  }

  public static class IdentityManagerConfiguration {

    private IdmMappingScript idmMapping;

    public IdmMappingScript getIdmMapping() {
      return idmMapping;
    }

    public void setIdmMapping(String path) throws IOException {
      this.idmMapping = new IdmMappingScript(path);
    }
  }

  public List<String> getWhiteList() {
    return whiteList;
  }

  public void setWhiteList(String whiteList) {
    this.whiteList = Arrays.asList(whiteList.split("\\s"));
  }

  public JwtConfiguration getJwt() {
    return jwt;
  }

  public void setJwt(JwtConfiguration jwt) {
    this.jwt = jwt;
  }

  public Map<String, ServiceProviderConfiguration> getServiceProviders() {
    return serviceProviders;
  }

  public void setServiceProviders(Map<String, ServiceProviderConfiguration> serviceProviders) {
    this.serviceProviders = serviceProviders;
  }

  public IdentityProviderConfiguration getIdentityProvider() {
    return identityProvider;
  }

  public void setIdentityProvider(IdentityProviderConfiguration identityProvider) {
    this.identityProvider = identityProvider;
  }

  public IdentityManagerConfiguration getIdentityManager() {
    return identityManager;
  }

  public void setIdentityManager(IdentityManagerConfiguration identityManager) {
    this.identityManager = identityManager;
  }

  public String getUsers() {
    return users;
  }

  public void setUsers(String users) {
    this.users = users;
  }

  public String getHomePageUrl() {
    return homePageUrl;
  }

  public void setHomePageUrl(String homePageUrl) {
    this.homePageUrl = homePageUrl;
  }

  public boolean isShowErrors() {
    return showErrors;
  }

  public void setShowErrors(boolean showErrors) {
    this.showErrors = showErrors;
  }

  public int getTokenRecordTimeout() {
    return tokenRecordTimeout;
  }

  public void setTokenRecordTimeout(int tokenRecordTimeout) {
    this.tokenRecordTimeout = tokenRecordTimeout;
  }

  public int getIdpRetryTimeout() {
    return idpRetryTimeout;
  }

  public void setIdpRetryTimeout(int idpRetryTimeout) {
    this.idpRetryTimeout = idpRetryTimeout;
  }

  public int getIdpMaxAttempts() {
    return idpMaxAttempts;
  }

  public void setIdpMaxAttempts(int idpMaxAttempts) {
    this.idpMaxAttempts = idpMaxAttempts;
  }

  public int getIdpValidateInterval() {
    return idpValidateInterval;
  }

  public void setIdpValidateInterval(int idpValidateInterval) {
    this.idpValidateInterval = idpValidateInterval;
  }
}
