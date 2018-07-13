package gov.ca.cwds.idm.util;

import gov.ca.cwds.idm.dto.UsersSearchParameter;

public class UsersSearchParametersUtil {

  private UsersSearchParametersUtil() {}

  public static final int DEFAULT_PAGESIZE = 60;

  public static UsersSearchParameter composeSearchParameter(String searchLastName) {
    if (searchLastName != null) {
      return UsersSearchParameter.SearchParameterBuilder.aSearchParameters()
          .withLastName(searchLastName)
          .withPageSize(DEFAULT_PAGESIZE)
          .build();
    } else {
      return UsersSearchParameter.SearchParameterBuilder.aSearchParameters()
          .withUserCounty(CurrentAuthenticatedUserUtil.getCurrentUserCountyName())
          .withPageSize(DEFAULT_PAGESIZE)
          .build();
    }
  }
}
