package gov.ca.cwds.util;

import static gov.ca.cwds.util.UniversalUserTokenDeserializer.ADMIN_OFFICE_IDS_PARAM;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.COUNTY_NAME_PARAM;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentAuthenticatedUserUtil {
  private CurrentAuthenticatedUserUtil() {}

  public static UniversalUserToken getCurrentUser() {
    Authentication authentication = getAuthentication();
    return(UniversalUserToken) authentication.getPrincipal();
  }

  public static String getCountyName(UniversalUserToken currentUser) {
    return (String) currentUser.getParameter(COUNTY_NAME_PARAM);
  }

  public static Set<String> getAdminOfficeIds(UniversalUserToken currentUser) {
    return (Set<String>) currentUser.getParameter(ADMIN_OFFICE_IDS_PARAM);
  }

  public static String getCurrentUserCountyName() {
    UniversalUserToken userToken = getCurrentUser();
    return getCountyName(userToken);
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
