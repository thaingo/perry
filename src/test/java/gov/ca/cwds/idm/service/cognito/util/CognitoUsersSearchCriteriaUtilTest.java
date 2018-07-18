package gov.ca.cwds.idm.service.cognito.util;

import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import org.junit.Test;

import static gov.ca.cwds.idm.service.cognito.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CognitoUsersSearchCriteriaUtilTest {

  @Test
  public void testComposeToGetByRacfid(){
    CognitoUsersSearchCriteria criteria = composeToGetByRacfid("ABC");
    assertThat(criteria.getSearchAttrName(), is(RACFID_STANDARD.getName()));
    assertThat(criteria.getSearchAttrValue(), is("ABC"));
    assertThat(criteria.getPageSize(), is(DEFAULT_PAGESIZE));
    assertThat(criteria.getPaginationToken(), is(nullValue()));
  }
}
