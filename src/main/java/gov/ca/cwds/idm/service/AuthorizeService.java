package gov.ca.cwds.idm.service;

import static gov.ca.cwds.config.api.idm.Roles.isMostlyCountyAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyOfficeAdmin;
import static gov.ca.cwds.config.api.idm.Roles.isMostlyStateAdmin;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service(value = "authorize")
@Profile("idm")
public class AuthorizeService {

  public static final String COUNTY_NAME = "county_name";

  public static boolean findUser(User user, UniversalUserToken admin) {

    if(isMostlyStateAdmin(admin)) {
      return true;

    } else if(isMostlyCountyAdmin(admin)) {
      String userCountyName = user.getCountyName();
      String adminCountyName = getAdminCountyName(admin);
      return areNotNullAndEquals(userCountyName, adminCountyName);

    } else if(isMostlyOfficeAdmin(admin)){
      String userOfficeName = user.getOffice();
      String adminOfficeId = getAdminOfficeId(admin);
      return areNotNullAndEquals(userOfficeName, adminOfficeId);
    }
    return false;
  }

  private static String getAdminCountyName(UniversalUserToken admin) {
    return (String)admin.getParameter(COUNTY_NAME);
  }

  private static String getAdminOfficeId(UniversalUserToken admin) {
    return (String)admin.getParameter("office_id");
  }

  static boolean areNotNullAndEquals(String str1, String str2) {
    return str1 != null && str2 != null && str1.equals(str2);
  }
}
