package gov.ca.cwds.config.api.idm;

import static gov.ca.cwds.util.Utils.toSet;

import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service(value = "roles")
@Profile("idm")
public class Roles {

  public static final String STATE_ADMIN = "State-admin";
  public static final String COUNTY_ADMIN = "County-admin";
  public static final String OFFICE_ADMIN = "Office-admin";
  public static final String CWS_WORKER = "CWS-worker";
  public static final String CALS_ADMIN = "CALS-admin";
  public static final String CALS_EXTERNAL_WORKER = "CALS-external-worker";
  public static final String IDM_JOB = "IDM-job";

  private Roles() {}

  public static Set<String> getAdminRoles() {
    Set<String> result = new HashSet<>(getCwsAdminRoles());
    result.add(CALS_ADMIN);
    return result;
  }

  public static Set<String> getCwsAdminRoles() {
    return toSet(COUNTY_ADMIN, STATE_ADMIN, OFFICE_ADMIN);
  }


}
