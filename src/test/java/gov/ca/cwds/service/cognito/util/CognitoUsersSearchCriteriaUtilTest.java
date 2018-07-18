package gov.ca.cwds.service.cognito.util;

import gov.ca.cwds.idm.service.cognito.dto.CognitoUsersSearchCriteria;
import org.junit.Test;

import static gov.ca.cwds.idm.service.cognito.CognitoUtils.RACFID_ATTR_NAME_STANDARD;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CognitoUsersSearchCriteriaUtilTest {

  @Test
  public void testComposeToGetByRacfid(){
    CognitoUsersSearchCriteria criteria = composeToGetByRacfid("ABC");
    assertThat(criteria.getAttrName(), is(RACFID_ATTR_NAME_STANDARD));
    assertThat(criteria.getAttrValue(), is("ABC"));
    assertThat(criteria.getPageSize(), is(DEFAULT_PAGESIZE));
    assertThat(criteria.getPaginationToken(), is(nullValue()));
  }
}
