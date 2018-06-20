package gov.ca.cwds.util;

import gov.ca.cwds.UniversalUserToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentAuthenticatedUserUtil {
  private CurrentAuthenticatedUserUtil() {}

  private static final String COUNTY_NAME_PARAM = "county_name";

  public static String getCurrentUserCountyName() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    UniversalUserToken userToken =
        (UniversalUserToken) securityContext.getAuthentication().getPrincipal();
    return (String) userToken.getParameter(COUNTY_NAME_PARAM);
  }
}
