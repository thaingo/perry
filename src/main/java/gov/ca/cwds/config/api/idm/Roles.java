package gov.ca.cwds.config.api.idm;

import static gov.ca.cwds.util.Utils.toSet;

import gov.ca.cwds.UniversalUserToken;
import java.util.Collections;
import java.util.Set;

public class Roles {

  private Roles() {
  }

  public static final String CWS_ADMIN = "CWS-admin";
  public static final String CWS_WORKER = "CWS-worker";
  public static final String CALS_EXTERNAL_WORKER = "CALS-external-worker";
  public static final String IDM_JOB = "IDM-job";
  public static final String COUNTY_ADMIN = "County-admin";
  public static final String STATE_ADMIN = "State-admin";
  public static final String OFFICE_ADMIN = "Office-admin";

  public static boolean isAdmin(UniversalUserToken user) {
    return !Collections.disjoint(user.getRoles(), getAdminRoles());
  }

  public static boolean isNonRacfIdCalsUser(UniversalUserToken user) {
    return user.getRoles().contains(CALS_EXTERNAL_WORKER);
  }

  static Set<String> getAdminRoles() {
    return toSet(CWS_ADMIN, COUNTY_ADMIN, STATE_ADMIN, OFFICE_ADMIN);
  }
}
