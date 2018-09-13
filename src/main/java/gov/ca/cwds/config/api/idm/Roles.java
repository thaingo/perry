package gov.ca.cwds.config.api.idm;

import static gov.ca.cwds.util.Utils.toSet;

import gov.ca.cwds.UniversalUserToken;
import java.util.Collections;
import java.util.Set;

public final class Roles {

  public static final String CWS_WORKER = "CWS-worker";
  public static final String CALS_EXTERNAL_WORKER = "CALS-external-worker";
  public static final String IDM_JOB = "IDM-job";
  public static final String COUNTY_ADMIN = "County-admin";
  public static final String STATE_ADMIN = "State-admin";
  public static final String OFFICE_ADMIN = "Office-admin";

  private Roles() {
  }

  public static boolean isAdmin(UniversalUserToken user) {
    Set<String> adminRoles = getAdminRoles();
    return !Collections.disjoint(user.getRoles(), adminRoles);
  }

  public static boolean isNonRacfIdCalsUser(UniversalUserToken user) {
    return user.getRoles().contains(CALS_EXTERNAL_WORKER);
  }

  static Set<String> getAdminRoles() {
    return toSet(COUNTY_ADMIN, STATE_ADMIN, OFFICE_ADMIN);
  }
}
