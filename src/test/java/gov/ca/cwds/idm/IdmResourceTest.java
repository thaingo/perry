package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertStrict;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.InternalErrorException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.service.CognitoServiceFacade;
import gov.ca.cwds.rest.api.domain.PerryException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import javax.annotation.PostConstruct;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

@ActiveProfiles({"dev", "idm"})
public class IdmResourceTest extends BaseLiquibaseTest {

  private final static String USER_NO_RACFID_ID = "2be3221f-8c2f-4386-8a95-a68f0282efb0";
  private final static String USER_WITH_RACFID_ID = "24051d54-9321-4dd2-a92f-6425d6c455be";
  private final static String USER_WITH_RACFID_AND_DB_DATA_ID = "d740ec1d-80ae-4d84-a8c4-9bed7a942f5b";
  private final static String ABSENT_USER_ID = "absentUserId";
  private final static String ERROR_USER_ID = "errorUserId";

  private static final MediaType CONTENT_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeClass
  public static void beforeClass() throws Exception {
    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
  }

  @Before
  public void before() {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .build();
  }

  @Test
  @WithAnonymousUser
  public void testGetPermissions() throws Exception {

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/idm/permissions"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
        .andReturn();

    assertStrict(result, "fixtures/idm/permissions/valid.json");
  }

  @Test
  public void testGetUserNoRacfId() throws Exception {
    testGetValidYoloUser(USER_NO_RACFID_ID,
        "fixtures/idm/get-user/no-racfid-valid.json");
  }

  @Test
  public void testGetUserWithRacfId() throws Exception {
    testGetValidYoloUser(USER_WITH_RACFID_ID,
        "fixtures/idm/get-user/with-racfid-valid.json");
  }

  @Test
  public void testGetUserWithRacfIdAndDbData() throws Exception {
    testGetValidYoloUser(USER_WITH_RACFID_AND_DB_DATA_ID,
        "fixtures/idm/get-user/with-racfid-and-db-data-valid.json");
  }

  @Test
  public void testGetAbsentUser() throws Exception {
    authenticate("Yolo", "CARES admin");

    mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/" + ABSENT_USER_ID))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();
  }

  @Test
  public void testGetUserError() throws Exception {
    authenticate("Yolo", "CARES admin");

    try {
      mockMvc.perform(MockMvcRequestBuilders.get("/idm/users/" + ERROR_USER_ID));
      fail("NestedServletException should be thrown");
    } catch (NestedServletException e) {
      assertTrue(e.getCause() instanceof PerryException);
    }
  }

  private void testGetValidYoloUser(String userId, String fixtureFilePath) throws Exception {
    authenticate("Yolo", "CARES admin");

    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/" + userId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
        .andReturn();

    assertNonStrict(result, fixtureFilePath);
  }

  private void authenticate(String county, String... roles) {
    UniversalUserToken userToken = new UniversalUserToken();
    userToken.setParameter("county_name", county);
    userToken.setRoles(new HashSet<>(Arrays.asList(roles)));

    TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(
        userToken, null);
    testingAuthenticationToken.setAuthenticated(true);

    SecurityContext ctx = SecurityContextHolder.createEmptyContext();
    SecurityContextHolder.setContext(ctx);
    ctx.setAuthentication(testingAuthenticationToken);
  }

  @Component
  static class TestPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException {
      if (beanName.equals("cognitoServiceFacade")) {
        return new TestCognitoServiceFacade();
      } else {
        return bean;
      }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
      return bean;
    }
  }

  static class TestCognitoServiceFacade extends CognitoServiceFacade {

    private final static String USERPOOL = "userpool";

    private AWSCognitoIdentityProvider cognito;

    @PostConstruct
    @Override
    public void init() {
      cognito = mock(AWSCognitoIdentityProvider.class);

      CognitoProperties properties = new CognitoProperties();
      properties.setIamAccessKeyId("iamAccessKeyId");
      properties.setIamSecretKey("iamSecretKey");
      properties.setUserpool(USERPOOL);
      properties.setRegion("us-east-2");

      setProperties(properties);
      setIdentityProvider(cognito);

      CognitoUser noRacfIdUser = cognitoUser(USER_NO_RACFID_ID, Boolean.TRUE,
          "FORCE_CHANGE_PASSWORD", date(2018, 5, 4),
          date(2018, 5, 30), "donzano@gmail.com", "Don",
          "Manzano", "Yolo", "RFA-rollout:Snapshot-rollout:", null);

      CognitoUser withRacfIdUser = cognitoUser(USER_WITH_RACFID_ID, Boolean.TRUE,
          "CONFIRMED", date(2018, 5, 4),
          date(2018, 5, 29), "julio@gmail.com", "Julio",
          "Iglecias", "Yolo", "Hotline-rollout", "YOLOD");

      CognitoUser withRacfIdAndDbDataUser = cognitoUser(USER_WITH_RACFID_AND_DB_DATA_ID, Boolean.TRUE,
          "CONFIRMED", date(2018, 5, 3),
          date(2018, 5, 31), "garcia@gmail.com", "Garcia",
          "Gonzales", "Yolo", "test", "SMITHBO");

      setUpGetAbsentUserRequestAndResult();

      setUpGetErrorUserRequestAndResult();
    }

    private CognitoUser cognitoUser(String id, Boolean enabled, String status, Date userCreateDate,
        Date lastModifiedDate, String email, String firstName, String lastName, String county,
        String permissions, String racfId) {

      CognitoUser cognitoUser = new CognitoUser(id, enabled, status, userCreateDate,
          lastModifiedDate, email, firstName, lastName, county,
          permissions, racfId);

      setUpGetUserRequestAndResult(cognitoUser);

      return cognitoUser;
    }

    private void setUpGetUserRequestAndResult(CognitoUser coqnitoUser) {

      AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
          .withUsername(coqnitoUser.getId()).withUserPoolId(USERPOOL);

      AdminGetUserResult getUserResult = new AdminGetUserResult();
      getUserResult.setUsername(coqnitoUser.getId());
      getUserResult.setEnabled(coqnitoUser.getEnabled());
      getUserResult.setUserStatus(coqnitoUser.getStatus());
      getUserResult.setUserCreateDate(coqnitoUser.getUserCreateDate());
      getUserResult.setUserLastModifiedDate(coqnitoUser.getLastModifiedDate());

      Collection<AttributeType> attrs = new ArrayList<>();

      if(coqnitoUser.getEmail() != null) {
        attrs.add(attr("email", coqnitoUser.getEmail()));
      }
      if(coqnitoUser.getFirstName() != null) {
        attrs.add(attr("given_name", coqnitoUser.getFirstName()));
      }
      if(coqnitoUser.getLastName() != null) {
        attrs.add(attr("family_name", coqnitoUser.getLastName()));
      }
      if(coqnitoUser.getCounty() != null) {
        attrs.add(attr("custom:County", coqnitoUser.getCounty()));
      }
      if(coqnitoUser.getPermissions() != null) {
        attrs.add(attr("custom:permission", coqnitoUser.getPermissions()));
      }
      if(coqnitoUser.getRacfId() != null) {
        attrs.add(attr("custom:RACFID", coqnitoUser.getRacfId()));
      }

      getUserResult.withUserAttributes(attrs);

      when(cognito.adminGetUser(getUserRequest))
          .thenReturn(getUserResult);
    }

    private void setUpGetAbsentUserRequestAndResult() {

      AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
          .withUsername(ABSENT_USER_ID).withUserPoolId(USERPOOL);

      when(cognito.adminGetUser(getUserRequest))
          .thenThrow(new UserNotFoundException("user not found"));
    }

    private void setUpGetErrorUserRequestAndResult() {

      AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
          .withUsername(ERROR_USER_ID).withUserPoolId(USERPOOL);

      when(cognito.adminGetUser(getUserRequest))
          .thenThrow(new InternalErrorException("internal error"));
    }
  }

  private static AttributeType attr(String name, String value) {
    AttributeType attr = new AttributeType();
    attr.setName(name);
    attr.setValue(value);
    return attr;
  }

  private static Date date(int year, int month, int dayOfMonth) {
    return java.sql.Date.valueOf((LocalDate.of(year, month, dayOfMonth)));
  }

  static class CognitoUser {
    private String id;
    private Boolean enabled;
    private String status;
    private Date userCreateDate;
    private Date lastModifiedDate;
    private String email;
    private String firstName;
    private String lastName;
    private String county;
    private String permissions;
    private String racfId;

    public CognitoUser(String id, Boolean enabled, String status, Date userCreateDate,
        Date lastModifiedDate, String email, String firstName, String lastName, String county,
        String permissions, String racfId) {
      this.id = id;
      this.enabled = enabled;
      this.status = status;
      this.userCreateDate = userCreateDate;
      this.lastModifiedDate = lastModifiedDate;
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
      this.county = county;
      this.permissions = permissions;
      this.racfId = racfId;
    }

    public String getId() {
      return id;
    }

    public Boolean getEnabled() {
      return enabled;
    }

    public String getStatus() {
      return status;
    }

    public Date getUserCreateDate() {
      return userCreateDate;
    }

    public Date getLastModifiedDate() {
      return lastModifiedDate;
    }

    public String getEmail() {
      return email;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public String getCounty() {
      return county;
    }

    public String getPermissions() {
      return permissions;
    }

    public String getRacfId() {
      return racfId;
    }
  }
}
