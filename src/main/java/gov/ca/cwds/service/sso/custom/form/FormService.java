package gov.ca.cwds.service.sso.custom.form;

import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import java.io.Serializable;
import java.util.Map;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.service.sso.SsoService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
public class FormService implements SsoService {

  @Override
  public Map getUserInfo(String ssoToken) {
    throw new UnsupportedOperationException("getUserInfo in dev mode");
  }

  @Override
  public void validate(PerryTokenEntity perryTokenEntity) {

  }

  @Override
  public void invalidate(String ssoToken) {

  }

  @Override
  public Serializable getSecurityContext() {
    return SecurityContextHolder.getContext();
  }


  @Override
  public String getSsoToken() {
    return getUniversalUserToken().getToken();
  }

  private UniversalUserToken getUniversalUserToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (UniversalUserToken) authentication.getPrincipal();
  }
}
