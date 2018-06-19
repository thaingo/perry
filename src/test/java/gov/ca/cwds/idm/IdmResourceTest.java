package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.service.CognitoUtils.PERMISSIONS_ATTR_NAME;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertStrict;
import static gov.ca.cwds.idm.util.UsersSearchParametersUtil.DEFAULT_PAGESIZE;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserResult;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserResult;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.InternalErrorException;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.idm.dto.UpdateUserDto;
import gov.ca.cwds.idm.service.CognitoServiceFacade;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import liquibase.util.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles({"dev", "idm"})
public class IdmResourceTest extends BaseLiquibaseTest {

  private final static String USER_NO_RACFID_ID = "2be3221f-8c2f-4386-8a95-a68f0282efb0";
  private final static String USER_WITH_RACFID_ID = "24051d54-9321-4dd2-a92f-6425d6c455be";
  private final static String USER_WITH_RACFID_AND_DB_DATA_ID = "d740ec1d-80ae-4d84-a8c4-9bed7a942f5b";
  private final static String ABSENT_USER_ID = "absentUserId";
  private final static String ERROR_USER_ID = "errorUserId";
  private final static String USERPOOL = "userpool";

  private static final MediaType CONTENT_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private CognitoServiceFacade cognitoServiceFacade;

  private MockMvc mockMvc;

  private AWSCognitoIdentityProvider cognito;

  @BeforeClass
  public static void beforeClass() throws Exception {
    runLiquibaseScript(CMS_STORE_URL, "liquibase/cms-data.xml");
  }

  @Before
  public void before() {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();
    cognito = cognitoServiceFacade.getIdentityProvider();
  }

  @Test
  @WithMockCustomUser
  public void testGetPermissions() throws Exception {

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/idm/permissions"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
        .andReturn();

    assertStrict(result, "fixtures/idm/permissions/valid.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetPermissionsWithOtherRole() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/idm/permissions"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testGetUserNoRacfId() throws Exception {
    testGetValidYoloUser(USER_NO_RACFID_ID,
        "fixtures/idm/get-user/no-racfid-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetUserWithRacfId() throws Exception {
    testGetValidYoloUser(USER_WITH_RACFID_ID,
        "fixtures/idm/get-user/with-racfid-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetUserWithRacfIdAndDbData() throws Exception {
    testGetValidYoloUser(USER_WITH_RACFID_AND_DB_DATA_ID,
        "fixtures/idm/get-user/with-racfid-and-db-data-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testGetAbsentUser() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/idm/users/" + ABSENT_USER_ID))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testGetUserError() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/idm/users/" + ERROR_USER_ID))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser(county = "Madera")
  public void testGetUserByOtherCountyAdmin() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/idm/users/" + USER_NO_RACFID_ID))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testGetUserWithOtherRole() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/idm/users/" + USER_NO_RACFID_ID))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testGetAllYoloUsers() throws Exception {

    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
        .andReturn();

    assertNonStrict(result, "fixtures/idm/get-users/all-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testSearchUsers() throws Exception {

    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users?lastName=Ma"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
        .andReturn();

    assertNonStrict(result, "fixtures/idm/get-users/search-valid.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testSearchUsersWithOtherRole() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/idm/users?lastName=Ma"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUser() throws Exception {

    UpdateUserDto updateUserDto = new UpdateUserDto();
    updateUserDto.setEnabled(Boolean.FALSE);
    updateUserDto.setPermissions(new HashSet<>(Arrays.asList("RFA-rollout", "Hotline-rollout")));

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(USER_NO_RACFID_ID,
            attr(PERMISSIONS_ATTR_NAME, "RFA-rollout:Hotline-rollout"));

    AdminDisableUserRequest disableUserRequest =
        setDisableUserRequestAndResult(USER_NO_RACFID_ID);

    mockMvc
        .perform(MockMvcRequestBuilders.patch("/idm/users/" + USER_NO_RACFID_ID)
            .contentType(CONTENT_TYPE)
            .content(asJsonString(updateUserDto)))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    verify(cognito, times(1))
        .adminUpdateUserAttributes(updateAttributesRequest);

    verify(cognito, times(1))
        .adminDisableUser(disableUserRequest);

    InOrder inOrder = inOrder(cognito);
    inOrder.verify(cognito).adminUpdateUserAttributes(updateAttributesRequest);
    inOrder.verify(cognito).adminDisableUser(disableUserRequest);
  }

  @Test
  @WithMockCustomUser
  public void testUpdateUserNoChanges() throws Exception {

    UpdateUserDto updateUserDto = new UpdateUserDto();
    updateUserDto.setEnabled(Boolean.TRUE);
    updateUserDto.setPermissions(new HashSet<>(Arrays.asList("RFA-rollout", "Snapshot-rollout")));

    AdminUpdateUserAttributesRequest updateAttributesRequest =
        setUpdateUserAttributesRequestAndResult(USER_NO_RACFID_ID,
            attr(PERMISSIONS_ATTR_NAME, "RFA-rollout:Snapshot-rollout"));

    AdminEnableUserRequest enableUserRequest = setEnableUserRequestAndResult(USER_NO_RACFID_ID);

    mockMvc.perform(MockMvcRequestBuilders.patch("/idm/users/" + USER_NO_RACFID_ID)
        .contentType(CONTENT_TYPE)
        .content(asJsonString(updateUserDto)))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    verify(cognito, times(0))
        .adminUpdateUserAttributes(updateAttributesRequest);

    verify(cognito, times(0))
        .adminEnableUser(enableUserRequest);
  }

  @Test
  @WithMockCustomUser(county = "Madera")
  public void testUpdateUserByOtherCountyAdmin() throws Exception {

    UpdateUserDto updateUserDto = new UpdateUserDto();
    updateUserDto.setEnabled(Boolean.FALSE);
    updateUserDto.setPermissions(new HashSet<>(Arrays.asList("RFA-rollout", "Hotline-rollout")));

    mockMvc.perform(MockMvcRequestBuilders.patch("/idm/users/" + USER_NO_RACFID_ID)
        .contentType(CONTENT_TYPE)
        .content(asJsonString(updateUserDto)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testUpdateUserWithOtherRole() throws Exception {

    UpdateUserDto updateUserDto = new UpdateUserDto();
    updateUserDto.setEnabled(Boolean.FALSE);
    updateUserDto.setPermissions(new HashSet<>(Arrays.asList("RFA-rollout", "Hotline-rollout")));

    mockMvc.perform(MockMvcRequestBuilders.patch("/idm/users/" + USER_NO_RACFID_ID)
        .contentType(CONTENT_TYPE)
        .content(asJsonString(updateUserDto)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsers() throws Exception {
    MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=SMITHBO"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-valid.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsersNoRacfId() throws Exception {
    MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=SMITHB1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-no-racfid.json");
  }

  @Test
  @WithMockCustomUser
  public void testVerifyUsersCognitoUserIsPresent() throws Exception {
    MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/users/verify?email=julio@gmail.com&racfid=SMITHBO"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-user-present.json");
  }

  @Test
  @WithMockCustomUser(roles = {"OtherRole"})
  public void testVerifyUserWithOtherRole() throws Exception {

    MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=CWDS"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andReturn();
  }

  @Test
  @WithMockCustomUser(county = "Madera")
  public void testVerifyUsersOtherCounty() throws Exception {
    MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.get("/idm/users/verify?email=test@test.com&racfid=SMITHBO"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/verify-user/verify-other-county.json");
  }


  private void testGetValidYoloUser(String userId, String fixtureFilePath) throws Exception {

    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/" + userId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
        .andReturn();

    assertNonStrict(result, fixtureFilePath);
  }

  private AdminUpdateUserAttributesRequest setUpdateUserAttributesRequestAndResult(
      String id,
      AttributeType... userAttributes) {
    AdminUpdateUserAttributesRequest request = new AdminUpdateUserAttributesRequest()
        .withUsername(id).withUserPoolId(USERPOOL).withUserAttributes(userAttributes);
    AdminUpdateUserAttributesResult result = new AdminUpdateUserAttributesResult();
    when(cognito.adminUpdateUserAttributes(request)).thenReturn(result);
    return request;
  }

  private AdminDisableUserRequest setDisableUserRequestAndResult(String id) {
    AdminDisableUserRequest request = new AdminDisableUserRequest()
        .withUsername(id).withUserPoolId(USERPOOL);
    AdminDisableUserResult result = new AdminDisableUserResult();
    when(cognito.adminDisableUser(request)).thenReturn(result);
    return request;
  }

  private AdminEnableUserRequest setEnableUserRequestAndResult(String id) {
    AdminEnableUserRequest request = new AdminEnableUserRequest()
        .withUsername(id).withUserPoolId(USERPOOL);
    AdminEnableUserResult result = new AdminEnableUserResult();
    when(cognito.adminEnableUser(request)).thenReturn(result);
    return request;
  }

  private static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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

  public static class TestCognitoServiceFacade extends CognitoServiceFacade {

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

      TestUser user0 = testUser(USER_NO_RACFID_ID, Boolean.TRUE,
          "FORCE_CHANGE_PASSWORD", date(2018, 5, 4),
          date(2018, 5, 30), "donzano@gmail.com", "Don",
          "Manzano", "Yolo", "RFA-rollout:Snapshot-rollout:", null);

      TestUser user1 = testUser(USER_WITH_RACFID_ID, Boolean.TRUE,
          "CONFIRMED", date(2018, 5, 4),
          date(2018, 5, 29), "julio@gmail.com", "Julio",
          "Iglecias", "Yolo", "Hotline-rollout", "YOLOD");

      TestUser user2 = testUser(USER_WITH_RACFID_AND_DB_DATA_ID, Boolean.TRUE,
          "CONFIRMED", date(2018, 5, 3),
          date(2018, 5, 31), "garcia@gmail.com", "Garcia",
          "Gonzales", "Yolo", "test", "SMITHBO");

      setUpGetAbsentUserRequestAndResult();

      setUpGetErrorUserRequestAndResult();

      setSearchYoloUsersRequestAndResult("", user0, user1, user2);

      setSearchYoloUsersRequestAndResult("Ma", user0);

      setSearchUsersByEmailRequestAndResult("julio@gmail.com", "test@test.com", user1);
    }

    private void setSearchYoloUsersRequestAndResult(String lastNameSubstr, TestUser... testUsers) {
      ListUsersRequest request =
          new ListUsersRequest()
              .withUserPoolId(USERPOOL)
              .withLimit(DEFAULT_PAGESIZE)
              .withFilter("preferred_username = \"Yolo\"");

      if (StringUtils.isNotEmpty(lastNameSubstr)) {
        request.withFilter("family_name ^= \"" + lastNameSubstr + "\"");
      }

      List<UserType> userTypes = Arrays.stream(testUsers)
          .map(testUser -> userType(testUser)).collect(Collectors.toList());

      ListUsersResult result = new ListUsersResult().withUsers(userTypes);

      when(cognito.listUsers(request)).thenReturn(result);
    }

    private TestUser testUser(String id, Boolean enabled, String status, Date userCreateDate,
        Date lastModifiedDate, String email, String firstName, String lastName, String county,
        String permissions, String racfId) {

      TestUser testUser = new TestUser(id, enabled, status, userCreateDate,
          lastModifiedDate, email, firstName, lastName, county,
          permissions, racfId);

      setUpGetUserRequestAndResult(testUser);

      return testUser;
    }

    private static Collection<AttributeType> attrs(TestUser testUser) {
      Collection<AttributeType> attrs = new ArrayList<>();

      if (testUser.getEmail() != null) {
        attrs.add(attr("email", testUser.getEmail()));
      }
      if (testUser.getFirstName() != null) {
        attrs.add(attr("given_name", testUser.getFirstName()));
      }
      if (testUser.getLastName() != null) {
        attrs.add(attr("family_name", testUser.getLastName()));
      }
      if (testUser.getCounty() != null) {
        attrs.add(attr("custom:County", testUser.getCounty()));
      }
      if (testUser.getPermissions() != null) {
        attrs.add(attr("custom:permission", testUser.getPermissions()));
      }
      if (testUser.getRacfId() != null) {
        attrs.add(attr("custom:RACFID", testUser.getRacfId()));
      }
      return attrs;
    }

    private static UserType userType(TestUser testUser) {
      UserType userType = new UserType()
          .withUsername(testUser.getId())
          .withEnabled(testUser.getEnabled())
          .withUserCreateDate(testUser.getUserCreateDate())
          .withUserLastModifiedDate(testUser.getLastModifiedDate())
          .withUserStatus(testUser.getStatus());

      userType.withAttributes(attrs(testUser));
      return userType;
    }

    private void setSearchUsersByEmailRequestAndResult(String email_correct, String email_wrong, TestUser... testUsers) {
      ListUsersRequest request_correct =
              new ListUsersRequest()
                      .withUserPoolId(USERPOOL)
                      .withFilter("email = \"" + email_correct + "\"");

      ListUsersRequest request_wrong =
              new ListUsersRequest()
                      .withUserPoolId(USERPOOL)
                      .withFilter("email = \"" + email_wrong + "\"");


      List<UserType> userTypes = Arrays.stream(testUsers)
              .map(TestCognitoServiceFacade::userType).collect(Collectors.toList());

      ListUsersResult result = new ListUsersResult().withUsers(userTypes);
      ListUsersResult result_empty = new ListUsersResult();

      when(cognito.listUsers(request_correct)).thenReturn(result);
      when(cognito.listUsers(request_wrong)).thenReturn(result_empty);
    }

    private void setUpGetUserRequestAndResult(TestUser testUser) {

      AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
          .withUsername(testUser.getId()).withUserPoolId(USERPOOL);

      AdminGetUserResult getUserResult = new AdminGetUserResult();
      getUserResult.setUsername(testUser.getId());
      getUserResult.setEnabled(testUser.getEnabled());
      getUserResult.setUserStatus(testUser.getStatus());
      getUserResult.setUserCreateDate(testUser.getUserCreateDate());
      getUserResult.setUserLastModifiedDate(testUser.getLastModifiedDate());

      getUserResult.withUserAttributes(attrs(testUser));

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

  static class TestUser {

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

    public TestUser(String id, Boolean enabled, String status, Date userCreateDate,
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
