package gov.ca.cwds.idm.service.cognito.util;

import static gov.ca.cwds.idm.service.cognito.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.util.Utils.toLowerCase;

import gov.ca.cwds.idm.service.cognito.StandardUserAttribute;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria.SearchCriteriaBuilder;

public class CognitoUsersSearchCriteriaUtil {

  public static final int DEFAULT_PAGESIZE = 60;

  private CognitoUsersSearchCriteriaUtil() {}

  public static CognitoUsersSearchCriteria composeToGetPage(String paginationToken) {
    return SearchCriteriaBuilder.aSearchParameters()
        .withPaginationToken(paginationToken)
        .withPageSize(DEFAULT_PAGESIZE)
        .build();
  }

  public static CognitoUsersSearchCriteria composeToGetFirstPageByAttribute(
      StandardUserAttribute attr, String value) {
    return SearchCriteriaBuilder.aSearchParameters()
        .withPageSize(DEFAULT_PAGESIZE)
        .withSearchAttr(attr, value)
        .build();
  }

  public static CognitoUsersSearchCriteria composeToGetFirstPageByEmail(String email) {
    return composeToGetFirstPageByAttribute(EMAIL, toLowerCase(email));
  }
}
