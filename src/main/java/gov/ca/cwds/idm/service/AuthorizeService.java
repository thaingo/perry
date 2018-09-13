package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.isMostlyCountyAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyOfficeAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyStateAdmin;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCountyName;
import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getCurrentUser;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service(value = "authorize")
@Profile("idm")
public class AuthorizeService {

  public boolean byUser(User user) {
    UniversalUserToken admin = getCurrentUser();
    return byUserAndAdmin(user, admin);
  }

   boolean byUserAndAdmin(User user, UniversalUserToken admin) {
    if(isMostlyStateAdmin(admin)) {
      return true;

    } else if(isMostlyCountyAdmin(admin)) {
      String userCountyName = user.getCountyName();
      String adminCountyName = getCountyName(admin);
      return areNotNullAndEquals(userCountyName, adminCountyName);

    } else if(isMostlyOfficeAdmin(admin)){
      String userOfficeName = user.getOffice();
      String adminOfficeId = getAdminOfficeId(admin);
      return areNotNullAndEquals(userOfficeName, adminOfficeId);
    }
    return false;
  }

  private static String getAdminOfficeId(UniversalUserToken admin) {
    return (String)admin.getParameter("office_id");
  }

  static boolean areNotNullAndEquals(String str1, String str2) {
    return str1 != null && str2 != null && str1.equals(str2);
  }
}
