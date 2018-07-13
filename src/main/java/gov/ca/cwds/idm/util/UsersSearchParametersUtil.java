package gov.ca.cwds.idm.util;

import gov.ca.cwds.idm.dto.UsersSearchParameter;
import gov.ca.cwds.util.CurrentAuthenticatedUserUtil;

public class UsersSearchParametersUtil {

  private UsersSearchParametersUtil() {}

  public static final int DEFAULT_PAGESIZE = 60;

  public static UsersSearchParameter composeToGetAllByPages(String paginationToken) {
    return UsersSearchParameter.SearchParameterBuilder.aSearchParameters()
            .withPaginationToken(paginationToken)
            .withPageSize(DEFAULT_PAGESIZE)
            .build();
  }
}
