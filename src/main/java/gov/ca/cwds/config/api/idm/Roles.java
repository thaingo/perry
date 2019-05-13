package gov.ca.cwds.config.api.idm;

import static gov.ca.cwds.util.Utils.toSet;

import com.google.common.collect.ImmutableMap;
import gov.ca.cwds.idm.dto.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service(value = "roles")
@Profile("idm")
public class Roles {

  public static final String SUPER_ADMIN = "Super-admin";
  public static final String STATE_ADMIN = "State-admin";
  public static final String COUNTY_ADMIN = "County-admin";
  public static final String OFFICE_ADMIN = "Office-admin";

  public static final String CWS_WORKER = "CWS-worker";
  public static final String CALS_EXTERNAL_WORKER = "CALS-external-worker";

  public static final String EXTERNAL_APP = "External-application";

  private static final String ID = "id";
  private static final String NAME = "name";

  private static List<Map<String, String>> roleList;

  private static Map<String, String> roleListReversed = new HashMap<>(7);

  private Roles() {}

  public static Set<String> getAdminRoles() {
    return toSet(SUPER_ADMIN, STATE_ADMIN, COUNTY_ADMIN, OFFICE_ADMIN);
  }



  /**
   * Static block to initialize roleList to be used as a dictionary service.
   */
  static {
    roleList = new ArrayList<>();
    roleList.add(ImmutableMap.of(ID, SUPER_ADMIN, NAME, "Global Administrator"));
    roleList.add(ImmutableMap.of(ID, STATE_ADMIN, NAME, "State Administrator"));
    roleList.add(ImmutableMap.of(ID, COUNTY_ADMIN, NAME, "County Administrator"));
    roleList.add(ImmutableMap.of(ID, OFFICE_ADMIN, NAME, "Office Administrator"));
    roleList.add(ImmutableMap.of(ID, CWS_WORKER, NAME, "CWS Worker"));
    roleList.add(ImmutableMap.of(ID, CALS_EXTERNAL_WORKER, NAME, "CALS External Worker"));
    for (Map<String, String> role: roleList) {
      roleListReversed.put(role.get(ID), role.get(NAME));
    }
  }

  /**
   * Return list of roleList with id and name to be used as a dictionary service.
   */
  public static List<Map<String, String>> findRoles() {
    return Roles.roleList;
  }

  public static List<String> getAllRolesUsedByUI() {
    return Roles.roleList.stream().map(m -> m.get(ID)).collect(Collectors.toList());
  }

  public static String toRolesKeysString(User user) {
    return joinRoles(user.getRoles());
  }

  public static String toRolesNamesString(User user) {
    return joinRoles(replaceRoleIdByName(user.getRoles()));
  }

  private static String joinRoles(Iterable<String> roles) {
    return StringUtils.join(roles, ", ");
  }

  public static String getRoleNameById(String id) {
    return roleListReversed.get(id) != null ? roleListReversed.get(id): id;
  }

  public static Set<String> replaceRoleIdByName(Set<String> roleIds) {
    return roleIds != null ? roleIds.stream().sorted().map(Roles::getRoleNameById)
        .collect(Collectors.toSet()) : null;
  }

}
