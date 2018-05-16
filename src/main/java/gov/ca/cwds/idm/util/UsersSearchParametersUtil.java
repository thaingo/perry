package gov.ca.cwds.idm.util;

import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.dto.UsersSearchParameter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class UsersSearchParametersUtil {

  private UsersSearchParametersUtil() {}

  private static final int DEFAULT_PAGESIZE = 60;
  private static final String COUNTY_NAME_PARAM = "county_name";

  public static UsersSearchParameter composeSearchParameter(String searchLastName) {
    if (searchLastName != null) {
      return UsersSearchParameter.SearchParameterBuilder.aSearchParameters()
          .withLastName(searchLastName)
          .withPageSize(DEFAULT_PAGESIZE)
          .build();
    } else {
      SecurityContext securityContext = SecurityContextHolder.getContext();
      UniversalUserToken userToken =
          (UniversalUserToken) securityContext.getAuthentication().getPrincipal();
      String currentUserCounty = (String) userToken.getParameter(COUNTY_NAME_PARAM);
      return UsersSearchParameter.SearchParameterBuilder.aSearchParameters()
          .withUserCounty(currentUserCounty)
          .withPageSize(DEFAULT_PAGESIZE)
          .build();
    }
  }
}
