package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_PASS;
import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_USER;
import static gov.ca.cwds.idm.service.TestHelper.user;
import static gov.ca.cwds.idm.service.TestHelper.userType;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UserUpdate;
import gov.ca.cwds.idm.service.MappingService;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import gov.ca.cwds.rest.api.domain.UserIdmValidationException;
import gov.ca.cwds.service.CwsUserInfoService;
import gov.ca.cwds.service.dto.CwsUserInfo;
import java.util.Collections;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles({"dev", "idm"})
@SpringBootTest(properties = {
    "perry.identityManager.idmBasicAuthUser=" + IDM_BASIC_AUTH_USER,
    "perry.identityManager.idmBasicAuthPass=" + IDM_BASIC_AUTH_PASS,
    "perry.identityManager.idmMapping=config/idm.groovy",
    "spring.jpa.hibernate.ddl-auto=none"
})
public class ValidationServiceTest {

  private static final String USER_ID = "17067e4e-270f-4623-b86c-b4d4fa527a34";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  private ValidationServiceImpl service;

  @Autowired
  private MappingService mappingService;

  private CognitoServiceFacade cognitoServiceFacadeMock = mock(CognitoServiceFacade.class);

  private CwsUserInfoService cwsUserInfoServiceMock = mock(CwsUserInfoService.class);

  @Before
  public void before() {
    service.setCognitoServiceFacade(cognitoServiceFacadeMock);
    service.setCwsUserInfoService(cwsUserInfoServiceMock);
    mappingService.setCwsUserInfoService(cwsUserInfoServiceMock);
  }

  @Test
  public void enrichUserByUpdateDtoNoChanges() {
    User user = new User();

    Boolean enabled = Boolean.TRUE;
    user.setEnabled(enabled);

    Set<String> permissions = toSet("permission");
    user.setPermissions(permissions);

    Set<String> roles = toSet("role");
    user.setRoles(roles);

    service.enrichUserByUpdateDto(user, new UserUpdate());

    assertThat(user.getEnabled(), is(enabled));
    assertThat(user.getPermissions(), is(permissions));
    assertThat(user.getRoles(), is(roles));
  }

  @Test
  public void enrichUserByUpdateDtoAllChanged() {
    User user = new User();
    Boolean enabled = Boolean.TRUE;
    user.setEnabled(enabled);

    Set<String> permissions = toSet("permission");
    user.setPermissions(permissions);

    Set<String> roles = toSet("role");
    user.setRoles(roles);

    UserUpdate updateUserDto = new UserUpdate();

    Boolean newEnabled = Boolean.FALSE;
    updateUserDto.setEnabled(newEnabled);

    Set<String> newPermissions = toSet("newPermission");
    updateUserDto.setPermissions(newPermissions);

    Set<String> newRoles = toSet("newRole");
    updateUserDto.setRoles(newRoles);

    service.enrichUserByUpdateDto(user, updateUserDto);

    assertThat(user.getEnabled(), is(newEnabled));
    assertThat(user.getPermissions(), is(newPermissions));
    assertThat(user.getRoles(), is(newRoles));
  }

  @Test
  public void testPerformValidation_throwsNoRacfIdInCWS() {
    final String NO_ACTIVE_USER_WITH_RACFID_IN_CMS_ERROR_MSG =
        "No user with RACFID: NOIDCMS found in CWSCMS";
    final String racfId = "NOIDCMS";
    when(cwsUserInfoServiceMock.getCwsUserByRacfId(racfId)).thenReturn(null);
    expectedException.expect(UserIdmValidationException.class);
    expectedException.expectMessage(NO_ACTIVE_USER_WITH_RACFID_IN_CMS_ERROR_MSG);
    service.validateActivateUser(racfId);
  }

  @Test
  public void testPerformValidation_throwsActiveRacfIdAlreadyInCognito() {
    final String ACTIVE_USER_WITH_RACFID_EXISTS_IN_COGNITO_ERROR_MSG =
        "Active User with RACFID: SMITHBO exists in Cognito";
    final String racfId = "SMITHBO";
    when(cwsUserInfoServiceMock.getCwsUserByRacfId(racfId)).thenReturn(new CwsUserInfo());
    UserType userType = userType(user(), USER_ID);
    when(cognitoServiceFacadeMock.searchAllPages(any()))
        .thenReturn(Collections.singletonList(userType));
    expectedException.expect(UserIdmValidationException.class);
    expectedException.expectMessage(ACTIVE_USER_WITH_RACFID_EXISTS_IN_COGNITO_ERROR_MSG);
    service.validateActivateUser(racfId);
  }
}
