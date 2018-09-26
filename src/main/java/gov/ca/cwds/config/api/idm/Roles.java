package gov.ca.cwds.config.api.idm;

import static gov.ca.cwds.util.Utils.toSet;

import gov.ca.cwds.UniversalUserToken;
import java.util.Collections;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service(value = "roles")
@Profile("idm")
public class Roles {

  public static final String CWS_WORKER = "CWS-worker";
  public static final String CALS_EXTERNAL_WORKER = "CALS-external-worker";
  public static final String IDM_JOB = "IDM-job";
  public static final String COUNTY_ADMIN = "County-admin";
  public static final String STATE_ADMIN = "State-admin";
  public static final String OFFICE_ADMIN = "Office-admin";
  public static final String CALS_ADMIN = "CALS-admin";

  private Roles() {}

  public static boolean isAdmin(UniversalUserToken user) {
    Set<String> adminRoles = getAdminRoles();
    return !Collections.disjoint(user.getRoles(), adminRoles);
  }

  public static boolean isOfficeAdmin(UniversalUserToken user) {
    return user.getRoles().contains(OFFICE_ADMIN);
  }

  public static boolean isCountyAdmin(UniversalUserToken user) {
    return user.getRoles().contains(COUNTY_ADMIN);
  }

  public static boolean isNonRacfIdCalsUser(UniversalUserToken user) {
    return user.getRoles().contains(CALS_EXTERNAL_WORKER);
  }

  public static boolean isMostlyStateAdmin(UniversalUserToken user) {
    return STATE_ADMIN.equals(getStrongestAdminRole(user));
  }

  public static boolean isMostlyCountyAdmin(UniversalUserToken user) {
    return COUNTY_ADMIN.equals(getStrongestAdminRole(user));
  }

  public static boolean isMostlyOfficeAdmin(UniversalUserToken user) {
    return OFFICE_ADMIN.equals(getStrongestAdminRole(user));
  }

  public static boolean isCalsAdmin(UniversalUserToken user) {
    return user.getRoles().contains(CALS_ADMIN);
  }

  static String getStrongestAdminRole(UniversalUserToken user) {

    if (isAdmin(user)) {
      Set<String> roles = user.getRoles();
      if (roles.contains(STATE_ADMIN)) {
        return STATE_ADMIN;
      } else if (roles.contains(COUNTY_ADMIN)) {
        return COUNTY_ADMIN;
      } else if (roles.contains(OFFICE_ADMIN)) {
        return OFFICE_ADMIN;
      }
    }
    return null;
  }

  static Set<String> getAdminRoles() {
    return toSet(COUNTY_ADMIN, STATE_ADMIN, OFFICE_ADMIN);
  }
}
