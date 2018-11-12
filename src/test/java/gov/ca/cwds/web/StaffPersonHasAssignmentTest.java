package gov.ca.cwds.web;

//import static gov.ca.cwds.util.LiquibaseUtils.CMS_STORE_URL;

import static gov.ca.cwds.util.LiquibaseUtils.CMS_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.runLiquibaseScript;

import gov.ca.cwds.BaseIntegrationTest;
import gov.ca.cwds.PerryApplication;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author CWDS TPT-2 Team
 */
@ActiveProfiles({"dev"})
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.identityManager.idmMapping=config/idm.groovy",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
    "spring.datasource.url=" + CMS_STORE_URL,
    "perry.whiteList=*",
    "perry.identityProvider.idpMapping=config/cognito.groovy",
    "perry.serviceProviders.default.identityMapping=config/default.groovy",
    "perry.serviceProviders.intake.identityMapping=config/intake.groovy",
    "perry.jwt.timeout=10",
    "security.oauth2.resource.revokeTokenUri=http://revoke.token.url",
    "security.oauth2.resource.userInfoUri=http://user.info.url",
    "security.oauth2.resource.logoutTokenUri=http://logout.token.url",
    "perry.idpMaxAttempts=2",
    "idpRetryTimeout=0",
    "idpValidateInterval=2",
    "perry.tokenRecordTimeout=240"
}, classes = {PerryApplication.class})
@RunWith(SpringRunner.class)
public class StaffPersonHasAssignmentTest extends BaseIntegrationTest {

  @Autowired
  CwsUserInfoService cwsUserInfoService;

  @BeforeClass
  public static void beforeClass() throws Exception {
    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
  }

  @Test
  public void mapCaseCarryingStaffPersonTest() throws Exception {
    CwsUserInfo cwsUserInfo1 = cwsUserInfoService.composeForUserAuthorization("LUCKY"); //0Sr user has assignments
    CwsUserInfo cwsUserInfo2 = cwsUserInfoService.composeForUserAuthorization("MCALLUM"); // 75D user doesn't have assignments

    Assert.assertTrue(cwsUserInfo1.getHasAssignments());
    Assert.assertFalse(cwsUserInfo2.getHasAssignments());
  }
}
