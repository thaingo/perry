package gov.ca.cwds.service.sso;

import java.util.Map;

public interface SsoService {
  Map getUserInfo(String ssoToken);
  String validate();
  void invalidate();
  String getSsoToken();
}
