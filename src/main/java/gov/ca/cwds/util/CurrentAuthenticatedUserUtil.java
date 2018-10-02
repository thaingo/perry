package gov.ca.cwds.util;

import static gov.ca.cwds.util.UniversalUserTokenDeserializer.ADMIN_OFFICE_IDS_PARAM;
import static gov.ca.cwds.util.UniversalUserTokenDeserializer.COUNTY_NAME_PARAM;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentAuthenticatedUserUtil {

  private static Supplier<UniversalUserToken> adminSupplier;

  private CurrentAuthenticatedUserUtil() {}

  public static UniversalUserToken getCurrentUser() {
    if (adminSupplier != null) {
      return adminSupplier.get();
    }
    Authentication authentication = getAuthentication();
    return (UniversalUserToken) authentication.getPrincipal();
  }

  public static String getCountyName(UniversalUserToken currentUser) {
    return (String) currentUser.getParameter(COUNTY_NAME_PARAM);
  }

  public static Set<String> getAdminOfficeIds(UniversalUserToken currentUser) {
    return (Set<String>) currentUser.getParameter(ADMIN_OFFICE_IDS_PARAM);
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

  public static void setAdminSupplier(
      Supplier<UniversalUserToken> adminSupplier) {
    CurrentAuthenticatedUserUtil.adminSupplier = adminSupplier;
  }

  public static void resetAdminSupplier() {
    adminSupplier = null;
  }

}
