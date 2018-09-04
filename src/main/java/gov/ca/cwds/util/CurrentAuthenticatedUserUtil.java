package gov.ca.cwds.util;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentAuthenticatedUserUtil {
  private CurrentAuthenticatedUserUtil() {}

  private static final String COUNTY_NAME_PARAM = "county_name";

  public static String getCurrentUserCountyName() {
    Authentication authentication = getAuthentication();
    UniversalUserToken userToken = (UniversalUserToken) authentication.getPrincipal();
    return (String) userToken.getParameter(COUNTY_NAME_PARAM);
  }

  public static String getSsoToken() {
    Authentication authentication = getAuthentication();
    return ((PerryTokenEntity) authentication.getDetails()).getToken();
  }

  public static Optional<String> getUserId() {
    Authentication authentication = getAuthentication();

    if(authentication!= null) {
      return Optional.of(authentication.getPrincipal().toString());
    } else {
      return Optional.empty();
    }
  }

  private static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
