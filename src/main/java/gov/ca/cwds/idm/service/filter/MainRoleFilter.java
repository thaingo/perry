package gov.ca.cwds.idm.service.filter;

import java.util.HashSet;
import java.util.Set;

public class MainRoleFilter {

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
//    return result;
    return roles;
  }

  static String getMainRole(Set<String> roles) {
    return roles.iterator().next();
  }
}
