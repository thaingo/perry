package gov.ca.cwds.idm.service;

import static gov.ca.cwds.BaseIntegrationTest.H2_DRIVER_CLASS_NAME;
import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_PASS;
import static gov.ca.cwds.BaseIntegrationTest.IDM_BASIC_AUTH_USER;
import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.idm.util.TestUtils.generateId;
import static gov.ca.cwds.util.LiquibaseUtils.CMS_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.TOKEN_STORE_URL;
import static gov.ca.cwds.util.LiquibaseUtils.createCmsDatabase;
import static gov.ca.cwds.util.LiquibaseUtils.createTokenStoreDatabase;
import static gov.ca.cwds.util.Utils.toSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacade;
import java.util.Date;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles({"dev", "idm"})
@SpringBootTest(properties = {
    "perry.identityManager.idmBasicAuthUser=" + IDM_BASIC_AUTH_USER,
    "perry.identityManager.idmBasicAuthPass=" + IDM_BASIC_AUTH_PASS,
    "perry.identityManager.idmMapping=config/idm.groovy",
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL
})
public class UserServiceTest {

  @MockBean
  private CognitoServiceFacade cognitoServiceFacadeMock;

  @Autowired
  private UserService service;


  @BeforeClass
  public static void prepareDatabases() throws Exception {
    Class.forName(H2_DRIVER_CLASS_NAME);
    createTokenStoreDatabase();
  }

  @Test
  public void testCreateUser() {
    User userDto = new User();
    userDto.setEmail("gonzales@gmail.com");
    userDto.setFirstName("Garcia");
    userDto.setLastName("Gonzales");
    userDto.setCountyName("Yolo");
    userDto.setRoles(toSet(CWS_WORKER));
    when(cognitoServiceFacadeMock.createUser(userDto)).thenReturn(createdUserType());
    User result = service.createUser(userDto);
    
    assertThat(result.getEnabled(), is(Boolean.TRUE));
    assertNotNull(result.getId());
    assertNotNull(result.getUserLastModifiedDate());
    assertNotNull(result.getUserCreateDate());
  }

  private static UserType createdUserType() {
    UserType userType = new UserType();
    userType.setUsername(generateId());
    userType.setEnabled(true);
    userType.setUserStatus("FORCE_CHANGE_PASSWORD");
    userType.setUserLastModifiedDate(new Date());
    userType.setUserCreateDate(new Date());
    return userType;
  }

}
