package gov.ca.cwds.idm.service.filter;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;

import java.util.HashSet;
import java.util.Set;

public class MainRoleFilter {

  private MainRoleFilter() {
  }

  /**
   * @param roles
   * @return  set with one element which is a main role if such is found or an empty set otherwise
   */
  public static Set<String> filter(Set<String> roles) {
    Set<String> result = new HashSet();
    if(roles == null || roles.isEmpty()){
      return result;
    }
    String mainRole = getMainRole(roles);
    if(mainRole != null) {
      result.add(mainRole);
    }
    return result;
  }

  private static String getMainRole(Set<String> roles) {
    int maxRoleValue = 0;
    String maxRole = null;

    for(String role : roles) {
      int roleValue = getRoleValue(role);
      if(roleValue > maxRoleValue){
        maxRoleValue = roleValue;
        maxRole = role;
      }
    }
    return maxRole;
  }

  private static int getRoleValue(String role) {
    switch (role) {
      case STATE_ADMIN:
        return 6;
      case COUNTY_ADMIN:
        return 5;
      case OFFICE_ADMIN:
        return 4;
      case CALS_ADMIN:
        return 3;
      case CWS_WORKER:
        return 2;
      case CALS_EXTERNAL_WORKER:
        return 1;
      default:
        return 0;
    }
  }
}
