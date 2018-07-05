package gov.ca.cwds.service.sso;

import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import java.io.Serializable;
import java.util.Map;

public interface SsoService {
  Map getUserInfo(String ssoToken);
  void validate(PerryTokenEntity perryTokenEntity);
  void invalidate(String ssoToken);
  String getSsoToken();
  Serializable getSecurityContext();
}
