package gov.ca.cwds.idm.service.authorization;

import static gov.ca.cwds.config.api.idm.Roles.CALS_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CALS_EXTERNAL_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.COUNTY_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.config.api.idm.Roles.OFFICE_ADMIN;
import static gov.ca.cwds.config.api.idm.Roles.STATE_ADMIN;

import gov.ca.cwds.RolesHolder;
import gov.ca.cwds.config.api.idm.Roles;
import java.util.Collections;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service("userRoleService")
public class UserRolesService {

  private UserRolesService() {
  }

  public static <T extends RolesHolder> boolean isCountyAdmin(T user) {
    return user.getRoles().contains(COUNTY_ADMIN);
  }

  public static <T extends RolesHolder> boolean isStateAdmin(T user) {
    return user.getRoles().contains(STATE_ADMIN);
  }

  public static <T extends RolesHolder> boolean isCalsExternalWorker(T user) {
    return user.getRoles().contains(CALS_EXTERNAL_WORKER);
  }

  public static <T extends RolesHolder> boolean isOfficeAdmin(T user) {
    return user.getRoles().contains(OFFICE_ADMIN);
  }

  public static <T extends RolesHolder> boolean isCalsAdmin(T user) {
    return user.getRoles().contains(CALS_ADMIN);
  }

  public static <T extends RolesHolder> boolean isAdmin(T user) {
    return !Collections.disjoint(user.getRoles(), Roles.getAdminRoles());
  }

  public static <T extends RolesHolder> boolean isCwsAdmin(T user) {
    return !Collections.disjoint(user.getRoles(), Roles.getCwsAdminRoles());
  }

  public static <T extends RolesHolder> boolean isNonRacfIdCalsUser(T user) {
    return isCalsExternalWorker(user);
  }

  public static <T extends RolesHolder> Optional<String> getStrongestCwsRole(T user) {
    if (user.getRoles().contains(STATE_ADMIN)) {
      return Optional.of(STATE_ADMIN);
    } else if (user.getRoles().contains(COUNTY_ADMIN)) {
      return Optional.of(COUNTY_ADMIN);
    } else if (user.getRoles().contains(OFFICE_ADMIN)) {
      return Optional.of(OFFICE_ADMIN);
    } else if (user.getRoles().contains(CWS_WORKER)) {
      return Optional.of(CWS_WORKER);
    } else {
      return Optional.empty();
    }
  }
}
