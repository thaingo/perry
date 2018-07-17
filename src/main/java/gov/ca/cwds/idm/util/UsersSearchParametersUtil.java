package gov.ca.cwds.idm.util;

import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;

public class UsersSearchParametersUtil {

  private UsersSearchParametersUtil() {}

  public static final int DEFAULT_PAGESIZE = 60;

  public static CognitoUsersSearchCriteria composeToGetAllByPages(String paginationToken) {
    return CognitoUsersSearchCriteria.SearchParameterBuilder.aSearchParameters()
            .withPaginationToken(paginationToken)
            .withPageSize(DEFAULT_PAGESIZE)
            .build();
  }
}
