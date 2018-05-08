package gov.ca.cwds.service.sso;

import java.io.Serializable;
import java.util.Map;

public interface SsoService {
  Map getUserInfo(String ssoToken);
  String validate(Serializable ssoContext);
  void invalidate(String ssoToken);
  String getSsoToken();
  Serializable getSecurityContext();
}
