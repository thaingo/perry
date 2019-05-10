package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.EXTERNAL_APP;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.SUPER_ADMIN;
import static gov.ca.cwds.idm.service.filter.MainRoleFilter.getMainRole;

import gov.ca.cwds.RolesHolder;
import gov.ca.cwds.config.api.idm.Roles;
import java.util.Collections;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("userRoleService")
@Profile("idm")
public class UserRolesService {

  private UserRolesService() {
  }

  public static <T extends RolesHolder> boolean hasCountyAdminRole(T user) {
    return hasRole(user, COUNTY_ADMIN);
  }

  public static <T extends RolesHolder> boolean isCountyAdmin(T user) {
    return isAdmin(user, COUNTY_ADMIN);
  }

  public static <T extends RolesHolder> boolean hasSuperAdminRole(T user) {
    return hasRole(user, SUPER_ADMIN);
  }

  public static <T extends RolesHolder> boolean isSuperAdmin(T user) {
    return isAdmin(user, SUPER_ADMIN);
  }

  public static <T extends RolesHolder> boolean hasStateAdminRole(T user) {
    return hasRole(user, STATE_ADMIN);
  }

  public static <T extends RolesHolder> boolean isStateAdmin(T user) {
    return isAdmin(user, STATE_ADMIN);
  }

  public static <T extends RolesHolder> boolean hasCalsExternalWorkerRole(T user) {
    return hasRole(user, CALS_EXTERNAL_WORKER);
  }

  public static <T extends RolesHolder> boolean isCalsExternalWorker(T user) {
    return CALS_EXTERNAL_WORKER.equals(getMainRole(user.getRoles()));
  }

  public static <T extends RolesHolder> boolean isCwsWorker(T user) {
    return CWS_WORKER.equals(getMainRole(user.getRoles()));
  }

  public static <T extends RolesHolder> boolean hasOfficeAdminRole(T user) {
    return hasRole(user, OFFICE_ADMIN);
  }

  public static <T extends RolesHolder> boolean isOfficeAdmin(T user) {
    return isAdmin(user, OFFICE_ADMIN);
  }

  public static <T extends RolesHolder> boolean hasIdmJobRole(T user) {
    return hasRole(user, EXTERNAL_APP);
  }

  private static <T extends RolesHolder> boolean hasRole(T user, String roleName) {
    return user.getRoles().contains(roleName);
  }

  public static <T extends RolesHolder> boolean isAdmin(T user) {
    return !Collections.disjoint(user.getRoles(), Roles.getAdminRoles());
  }

  public static <T extends RolesHolder> boolean isNonRacfIdCalsUser(T user) {
    return hasCalsExternalWorkerRole(user);
  }

  public static <T extends RolesHolder> String getStrongestAdminRole(T user) {

    if (!isAdmin(user)) {
      throw new IllegalStateException("Unexpected user role. Admin is expected");
    }
    if (user.getRoles().contains(SUPER_ADMIN)) {
      return SUPER_ADMIN;
    } else if (user.getRoles().contains(STATE_ADMIN)) {
      return STATE_ADMIN;
    } else if (user.getRoles().contains(COUNTY_ADMIN)) {
      return COUNTY_ADMIN;
    } else {
      return OFFICE_ADMIN;
    }
  }

  private static <T extends RolesHolder> boolean isAdmin(T user, String strongestAdminRole) {
    return isAdmin(user) && (strongestAdminRole.equals(getStrongestAdminRole(user)));
  }
}
