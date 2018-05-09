package gov.ca.cwds.service;

import gov.ca.cwds.UniversalUserToken;

/**
 * Created by TPT2 on 10/24/2017.
 */
public interface LoginService {
  String issueAccessCode(String providerId);

  UniversalUserToken validate(String perryToken);

  void invalidate(String perryToken);

  String issueToken(String accessCode);
}
