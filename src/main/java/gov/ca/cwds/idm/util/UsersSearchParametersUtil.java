package gov.ca.cwds.idm.util;

import gov.ca.cwds.idm.dto.UsersSearchCriteria;

public class UsersSearchParametersUtil {

  private UsersSearchParametersUtil() {}

  public static final int DEFAULT_PAGESIZE = 60;

  public static UsersSearchCriteria composeToGetAllByPages(String paginationToken) {
    return UsersSearchCriteria.SearchParameterBuilder.aSearchParameters()
            .withPaginationToken(paginationToken)
            .withPageSize(DEFAULT_PAGESIZE)
            .build();
  }
}
