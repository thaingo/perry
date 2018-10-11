package gov.ca.cwds.config.api.idm;

import static gov.ca.cwds.util.Utils.toSet;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  private static List<Map<String, String>> roleList;

  private Roles() {}

  public static Set<String> getAdminRoles() {
    return toSet(COUNTY_ADMIN, STATE_ADMIN, OFFICE_ADMIN, CALS_ADMIN);
  }

  /**
   * Static block to initialize roleList to be used as a dictionary service.
   */
  static {
    roleList = new ArrayList<>();
    roleList.add(ImmutableMap.of("id", STATE_ADMIN, "name", "State Administrator"));
    roleList.add(ImmutableMap.of("id", COUNTY_ADMIN, "name", "County Administrator"));
    roleList.add(ImmutableMap.of("id", OFFICE_ADMIN, "name", "Office Administrator"));
    roleList.add(ImmutableMap.of("id", CALS_ADMIN, "name", "CALS Administrator"));
    roleList.add(ImmutableMap.of("id", CWS_WORKER, "name", "CWS Worker"));
    roleList.add(ImmutableMap.of("id", CALS_EXTERNAL_WORKER, "name", "CALS External Worker"));
  }

  /**
   * Return list of roleList with id and name to be used as a dictionary service.
   */
  public static List<Map<String, String>> findRoles() {
    return Roles.roleList;
  }
}
