package gov.ca.cwds.idm.util;

import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.COUNTY;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.FAILED_LOGINS_COUNT;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.OFFICE;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PERMISSIONS;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.PHONE_EXTENSION;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.RACFID_CUSTOM;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.RACFID_CUSTOM_2;
import static gov.ca.cwds.idm.service.cognito.attribute.CustomUserAttribute.ROLES;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.FIRST_NAME;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.LAST_NAME;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.PHONE_NUMBER;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.RACFID_STANDARD;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.DEFAULT_PAGESIZE;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUsersSearchCriteriaUtil.composeToGetFirstPageByAttribute;
import static gov.ca.cwds.PerryProperties.IdentityManagerConfiguration.numberOfFailedLoginsToLock;
import static gov.ca.cwds.idm.util.TestUtils.attr;
import static gov.ca.cwds.idm.util.TestUtils.date;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
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
import gov.ca.cwds.idm.service.cognito.CognitoProperties;
import gov.ca.cwds.idm.service.cognito.CognitoServiceFacadeImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import liquibase.util.StringUtils;

public class TestCognitoServiceFacade extends CognitoServiceFacadeImpl {

  public static final String USER_NO_RACFID_ID = "2be3221f-8c2f-4386-8a95-a68f0282efb0";
  public static final String USER_CALS_EXTERNAL = "2be3551f-8c9w-4386-8a95-a68f0777efb0";
  public static final String USER_WITH_RACFID_ID = "24051d54-9321-4dd2-a92f-6425d6c455be";
  public static final String USER_WITH_RACFID_AND_DB_DATA_ID =
      "d740ec1d-80ae-4d84-a8c4-9bed7a942f5b";
  public static final String USER_WITH_LESS_THEN_MAX_LOGIN_FAILURES_UNLOCKED = "24932d54-9321-4dd2-a92f-7425q6c411be";
  public static final String USER_WITH_NO_LOGIN_FAILURE_UNLOCKED = "24732d50-9421-4dd2-a92f-7425q6c411bx";
  public static final String USER_WITH_MORE_THEN_MAX_FAILURES_LOCKED = "44732d50-9425-4rd2-a92f-7425q0c411bw";
  public static final String USER_WITH_EXACT_NUMBER_LOGIN_FAILURES_LOCKED = "94732b50-9125-4rd2-a22f-7425q0c433bw";
  public static final String USER_WITH_RACFID_AND_INVALID_COUNTY_IN_COGNITO =
      "145614ce-0168-4950-9b47-7ba0cdf1f299";
  public static final String USER_WITH_NO_PHONE_EXTENSION = "d740ec1d-66ae-4d84-a8c4-8bed7a942f5b";
  public static final String NEW_USER_SUCCESS_ID = "17067e4e-270f-4623-b86c-b4d4fa527a34";
  public static final String NEW_USER_ES_FAIL_ID = "08e14c57-6e5e-48dd-8172-e8949c2a7f76";
  public static final String ES_ERROR_CREATE_USER_EMAIL = "es.error@create.com";
  public static final String SOME_PAGINATION_TOKEN = "somePaginationToken";
  public static final String ABSENT_USER_ID = "absentUserId";
  public static final String ERROR_USER_ID = "errorUserId";
  public static final String INACTIVE_USER_WITH_NO_ACTIVE_RACFID_IN_CMS =
      "17067e4e-270f-4623-b86c-b4d4fa527z79";
  public static final String INACTIVE_USER_WITH_ACTIVE_RACFID_IN_CMS =
      "17067e4e-270f-4623-b86c-b4d4fa524f38";
  public static final String USER_WITH_INACTIVE_STATUS_COGNITO =
      "17067e4e-270f-4623-b86c-b4d4fa527a22";
  public static final String INACTIVE_USER_WITH_NO_RACFID =
      "17067e4e-270f-4623-b86c-b4d4fa525d68";
  public static final String STATE_ADMIN_ID = "2d9369b4-5855-4a2c-95f7-3617fab1496a";
  public static final String COUNTY_ADMIN_ID = "c3702f4c113f1d2415447c8bfe8321d8df2d5151";
  public static final String SUPER_ADMIN_ID = "a3427f8e-b080-4f71-9514-840c1fcb505b";

  public static final String USERPOOL = "userpool";

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

    initUsers();

    TestUser userWithoutRacfid =
        testUser(
            USER_NO_RACFID_ID,
            Boolean.TRUE,
            "FORCE_CHANGE_PASSWORD",
            date(2018, 5, 4),
            date(2018, 5, 30),
            "donzano@gmail.com",
            "Don",
            "Manzano",
            WithMockCustomUser.COUNTY,
            "RFA-rollout:Snapshot-rollout:",
            "CWS-worker:County-admin",
            null,
            null,
            null,
            null,
            null);

    TestUser userWithRacfid =
        testUser(
            USER_WITH_RACFID_ID,
            Boolean.TRUE,
            "CONFIRMED",
            date(2018, 5, 4),
            date(2018, 5, 29),
            "julio@gmail.com",
            "Julio",
            "Iglecias",
            WithMockCustomUser.COUNTY,
            "Hotline-rollout",
            "CWS-worker:County-admin",
            "YOLOD",
            null,
            null,
            null,
            null);

    TestUser userWithRacfidAndDbData =
        testUser(
            USER_WITH_RACFID_AND_DB_DATA_ID,
            Boolean.TRUE,
            "CONFIRMED",
            date(2018, 5, 3),
            date(2018, 5, 31),
            "garcia@gmail.com",
            "Garcia",
            "Gonzales",
            WithMockCustomUser.COUNTY,
            "Hotline-rollout",
            "CWS-worker",
            "SMITHBO",
            WithMockCustomUser.OFFICE_ID,
            "+4646888777",
            "7",
            null);

    TestUser userWithNoPhoneExtension =
        testUser(
            USER_WITH_NO_PHONE_EXTENSION,
            Boolean.TRUE,
            "CONFIRMED",
            date(2018, 5, 3),
            date(2018, 5, 31),
            "gabriel@gmail.com",
            "Gabriel",
            "Huanito",
            WithMockCustomUser.COUNTY,
            "test",
            "CWS-worker",
            "SMITHB2",
            null,
            "+4646888777",
            "7",
            null);

    TestUser userWithEnableStatusInactiveInCognito =
        testUser(
            USER_WITH_INACTIVE_STATUS_COGNITO,
            Boolean.FALSE,
            "CONFIRMED",
            date(2018, 5, 3),
            date(2018, 5, 31),
            "smith3rd@gmail.com",
            "Smith",
            "Third",
            WithMockCustomUser.COUNTY,
            "test",
            null,
            "SMITHB3",
            null,
            "+4646888777",
            "7",
            null);

    TestUser userWithNoActiveRacfIdInCms =
        testUser(
            INACTIVE_USER_WITH_NO_ACTIVE_RACFID_IN_CMS,
            Boolean.FALSE,
            "CONFIRMED",
            date(2018, 5, 3),
            date(2018, 5, 31),
            "smith4th@gmail.com",
            "Smith",
            "Forth",
            WithMockCustomUser.COUNTY,
            "test",
            null,
            "NOIDCMS",
            null,
            "+7186888777",
            "7",
            null);

    TestUser userWithActiveRacfIdAInCms =
        testUser(
            INACTIVE_USER_WITH_ACTIVE_RACFID_IN_CMS,
            Boolean.FALSE,
            "CONFIRMED",
            date(2018, 5, 3),
            date(2018, 5, 31),
            "smith5th@gmail.com",
            "Smith",
            "Fifth",
            WithMockCustomUser.COUNTY,
            "test",
            null,
            "SMITHBO",
            null,
            "+4646888777",
            "7",
            null);

    TestUser inactiveUserWithNoRacfId =
        testUser(
            INACTIVE_USER_WITH_NO_RACFID,
            Boolean.FALSE,
            "CONFIRMED",
            date(2018, 5, 3),
            date(2018, 5, 31),
            "smith6th@gmail.com",
            "Smith",
            "Sixth",
            WithMockCustomUser.COUNTY,
            "test",
            null,
            null,
            null,
            null,
            null,
            null);

    setUpGetAbsentUserRequestAndResult();

    setUpGetErrorUserRequestAndResult();

    setListUsersRequestAndResult("", userWithoutRacfid, userWithRacfid, userWithRacfidAndDbData);

    setListUsersRequestAndResult(SOME_PAGINATION_TOKEN, userWithoutRacfid);

    setSearchUsersByEmailRequestAndResult("julio@gmail.com", "test@test.com", userWithRacfid);

    setSearchByRacfidRequestAndResult(userWithRacfid);

    setSearchByRacfidRequestAndResult(userWithRacfidAndDbData);

    setSearchByRacfidRequestAndResult(userWithNoPhoneExtension);

    setSearchByRacfidRequestAndResult(userWithEnableStatusInactiveInCognito);

    setSearchByRacfidRequestAndResult(userWithNoActiveRacfIdInCms);

    setSearchByRacfidRequestAndResult(inactiveUserWithNoRacfId);

    setSearchByRacfidRequestAndReturnResults(userWithActiveRacfIdAInCms, userWithRacfidAndDbData);
  }

  private void initUsers() {

    testUser(
        USER_CALS_EXTERNAL,
        Boolean.TRUE,
        "FORCE_CHANGE_PASSWORD",
        date(2018, 5, 4),
        date(2018, 5, 30),
        "donzano@gmail.com",
        "Don",
        "Manzano",
        WithMockCustomUser.COUNTY,
        "RFA-rollout:Snapshot-rollout:",
        "CALS-external-worker",
        null,
        null,
        null,
        null,
        null);

    testUser(
        USER_WITH_RACFID_AND_INVALID_COUNTY_IN_COGNITO,
        Boolean.TRUE,
        "CONFIRMED",
        date(2017, 5, 22),
        date(2018, 12, 3),
        "stan.hailen@osi.ca.gav",
        "Stan",
        "Hailen",
        "Lake",
        "Facility-search-rollout",
        "CWS-worker",
        "HAILES",
        WithMockCustomUser.OFFICE_ID,
        "+9167777777",
        "777",
        null);

    //countyAdminUser =
    testUser(
        COUNTY_ADMIN_ID,
        Boolean.TRUE,
        "CONFIRMED",
        date(2018, 5, 3),
        date(2018, 5, 31),
        "jkuser@gmail.com",
        "Reddy",
        "Jkuser",
        WithMockCustomUser.COUNTY,
        "test",
        "County-admin",
        "MCALLUM",
        WithMockCustomUser.OFFICE_ID,
        "+7680797987",
        null,
        null);

    //stateAdminUser =
    testUser(
        STATE_ADMIN_ID,
        Boolean.TRUE,
        "CONFIRMED",
        date(2018, 5, 3),
        date(2018, 5, 31),
        "yull@gmail.com",
        "Christina",
        "Yull",
        WithMockCustomUser.COUNTY,
        "test",
        "State-admin",
        "YULLC",
        WithMockCustomUser.OFFICE_ID,
        "+3254545345",
        null,
        null);

    testUser(
        USER_WITH_LESS_THEN_MAX_LOGIN_FAILURES_UNLOCKED,
        Boolean.TRUE,
        "CONFIRMED",
        date(2018, 5, 4),
        date(2018, 5, 29),
        "julio1@gmail.com",
        "Julio",
        "Iglecias",
        WithMockCustomUser.COUNTY,
        "Hotline-rollout",
        "CWS-worker:County-admin",
        "YOLOD",
        null,
        null,
        null,
        String.valueOf(numberOfFailedLoginsToLock - 1));

    testUser(
        USER_WITH_NO_LOGIN_FAILURE_UNLOCKED,
        Boolean.TRUE,
        "CONFIRMED",
        date(2018, 5, 4),
        date(2018, 5, 29),
        "julio2@gmail.com",
        "Julio",
        "Iglecias",
        WithMockCustomUser.COUNTY,
        "Hotline-rollout",
        "CWS-worker:County-admin",
        "YOLOD",
        null,
        null,
        null,
        null);

    testUser(
        USER_WITH_MORE_THEN_MAX_FAILURES_LOCKED,
        Boolean.TRUE,
        "CONFIRMED",
        date(2018, 5, 4),
        date(2018, 5, 29),
        "julio77@gmail.com",
        "Julio",
        "Iglecias",
        WithMockCustomUser.COUNTY,
        "Hotline-rollout",
        "CWS-worker:County-admin",
        "YOLOD",
        null,
        null,
        null,
        String.valueOf(numberOfFailedLoginsToLock + 1));

    testUser(
        USER_WITH_EXACT_NUMBER_LOGIN_FAILURES_LOCKED,
        Boolean.TRUE,
        "CONFIRMED",
        date(2018, 5, 4),
        date(2018, 5, 29),
        "julio78@gmail.com",
        "Julio",
        "Iglecias",
        WithMockCustomUser.COUNTY,
        "Hotline-rollout",
        "CWS-worker:County-admin",
        "YOLOD",
        null,
        null,
        null,
        String.valueOf(numberOfFailedLoginsToLock));

    testUser(
        NEW_USER_SUCCESS_ID,
        Boolean.TRUE,
        "FORCE_CHANGE_PASSWORD",
        date(2018, 5, 4),
        date(2018, 5, 30),
        "gonzales@gmail.com",
        "Garcia",
        "Gonzales",
        WithMockCustomUser.COUNTY,
        null,
        null,
        null,
        null,
        null,
        null,
        null);

    testUser(
        NEW_USER_ES_FAIL_ID,
        Boolean.TRUE,
        "FORCE_CHANGE_PASSWORD",
        date(2018, 5, 4),
        date(2018, 5, 30),
        ES_ERROR_CREATE_USER_EMAIL,
        "Garcia",
        "Gonzales",
        WithMockCustomUser.COUNTY,
        null,
        null,
        null,
        null,
        null,
        null,
        null);

    testUser(
        SUPER_ADMIN_ID,
        Boolean.TRUE,
        "CONFIRMED",
        date(2018, 9, 14),
        date(2018, 10, 3),
        "maygosh@gmail.com",
        "May",
        "Gosh",
        WithMockCustomUser.COUNTY,
        null,
        "Super-admin",
        null,
        null,
        null,
        null,
        null);

  }

  private void setListUsersRequestAndResult(String paginationToken, TestUser... testUsers) {
    ListUsersRequest request =
        new ListUsersRequest().withUserPoolId(USERPOOL).withLimit(DEFAULT_PAGESIZE);

    if (StringUtils.isNotEmpty(paginationToken)) {
      request.withPaginationToken(paginationToken);
    }

    List<UserType> userTypes =
        Arrays.stream(testUsers)
            .map(TestCognitoServiceFacade::userType)
            .collect(Collectors.toList());

    ListUsersResult result = new ListUsersResult().withUsers(userTypes);

    when(cognito.listUsers(request)).thenReturn(result);
  }

  private TestUser testUser(
      String id,
      Boolean enabled,
      String status,
      Date userCreateDate,
      Date lastModifiedDate,
      String email,
      String firstName,
      String lastName,
      String county,
      String permissions,
      String roles,
      String racfId,
      String officeId,
      String phoneNumber,
      String phoneExtension,
      String failedLoginsCount) {

    TestUser testUser =
        new TestUser(
            id,
            enabled,
            status,
            userCreateDate,
            lastModifiedDate,
            email,
            firstName,
            lastName,
            county,
            permissions,
            roles,
            racfId,
            officeId,
            phoneNumber,
            phoneExtension,
            failedLoginsCount);

    setUpGetUserRequestAndResult(testUser);

    return testUser;
  }

  private static Collection<AttributeType> attrs(TestUser testUser) {
    Collection<AttributeType> attrs = new ArrayList<>();

    if (testUser.getEmail() != null) {
      attrs.add(attr(EMAIL, testUser.getEmail()));
    }
    if (testUser.getFirstName() != null) {
      attrs.add(attr(FIRST_NAME, testUser.getFirstName()));
    }
    if (testUser.getLastName() != null) {
      attrs.add(attr(LAST_NAME, testUser.getLastName()));
    }
    if (testUser.getCounty() != null) {
      attrs.add(attr(COUNTY, testUser.getCounty()));
    }
    if (testUser.getPermissions() != null) {
      attrs.add(attr(PERMISSIONS, testUser.getPermissions()));
    }
    if (testUser.getRoles() != null) {
      attrs.add(attr(ROLES, testUser.getRoles()));
    }
    if (testUser.getRacfId() != null) {
      attrs.add(attr(RACFID_CUSTOM, testUser.getRacfId()));
      attrs.add(attr(RACFID_STANDARD, testUser.getRacfId()));
      attrs.add(attr(RACFID_CUSTOM_2, testUser.getRacfId()));
    }
    if (testUser.getOfficeId() != null) {
      attrs.add(attr(OFFICE, testUser.getOfficeId()));
    }
    if (testUser.getPhoneNumber() != null) {
      attrs.add(attr(PHONE_NUMBER, testUser.getPhoneNumber()));
    }
    if (testUser.getPhoneExtension() != null) {
      attrs.add(attr(PHONE_EXTENSION, testUser.getPhoneExtension()));
    }
    if (testUser.getFailedLoginsCount() != null) {
      attrs.add(attr(FAILED_LOGINS_COUNT, testUser.getFailedLoginsCount()));
    }
    return attrs;
  }

  private static UserType userType(TestUser testUser) {
    UserType userType =
        new UserType()
            .withUsername(testUser.getId())
            .withEnabled(testUser.getEnabled())
            .withUserCreateDate(testUser.getUserCreateDate())
            .withUserLastModifiedDate(testUser.getLastModifiedDate())
            .withUserStatus(testUser.getStatus());

    userType.withAttributes(attrs(testUser));
    return userType;
  }

  private void setSearchUsersByEmailRequestAndResult(
      String email_correct, String email_wrong, TestUser... testUsers) {
    ListUsersRequest request_correct =
        new ListUsersRequest()
            .withUserPoolId(USERPOOL)
            .withLimit(DEFAULT_PAGESIZE)
            .withFilter("email = \"" + email_correct + "\"");

    ListUsersRequest request_wrong =
        new ListUsersRequest()
            .withUserPoolId(USERPOOL)
            .withLimit(DEFAULT_PAGESIZE)
            .withFilter("email = \"" + email_wrong + "\"");

    List<UserType> userTypes =
        Arrays.stream(testUsers)
            .map(TestCognitoServiceFacade::userType)
            .collect(Collectors.toList());

    ListUsersResult result = new ListUsersResult().withUsers(userTypes);
    ListUsersResult result_empty = new ListUsersResult();

    when(cognito.listUsers(request_correct)).thenReturn(result);
    when(cognito.listUsers(request_wrong)).thenReturn(result_empty);
  }

  private void setUpGetUserRequestAndResult(TestUser testUser) {

    AdminGetUserRequest getUserRequest =
        new AdminGetUserRequest().withUsername(testUser.getId()).withUserPoolId(USERPOOL);

    AdminGetUserResult getUserResult = new AdminGetUserResult();
    getUserResult.setUsername(testUser.getId());
    getUserResult.setEnabled(testUser.getEnabled());
    getUserResult.setUserStatus(testUser.getStatus());
    getUserResult.setUserCreateDate(testUser.getUserCreateDate());
    getUserResult.setUserLastModifiedDate(testUser.getLastModifiedDate());

    getUserResult.withUserAttributes(attrs(testUser));

    when(cognito.adminGetUser(getUserRequest)).thenReturn(getUserResult);
  }

  private void setUpGetAbsentUserRequestAndResult() {

    AdminGetUserRequest getUserRequest =
        new AdminGetUserRequest().withUsername(ABSENT_USER_ID).withUserPoolId(USERPOOL);

    when(cognito.adminGetUser(getUserRequest))
        .thenThrow(new UserNotFoundException("user not found"));
  }

  private void setUpGetErrorUserRequestAndResult() {

    AdminGetUserRequest getUserRequest =
        new AdminGetUserRequest().withUsername(ERROR_USER_ID).withUserPoolId(USERPOOL);

    when(cognito.adminGetUser(getUserRequest))
        .thenThrow(new InternalErrorException("internal error"));
  }

  AdminUpdateUserAttributesRequest setUpdateUserAttributesRequestAndResult(
      String id, AttributeType... userAttributes) {
    AdminUpdateUserAttributesRequest request =
        new AdminUpdateUserAttributesRequest()
            .withUsername(id)
            .withUserPoolId(USERPOOL)
            .withUserAttributes(userAttributes);
    AdminUpdateUserAttributesResult result = new AdminUpdateUserAttributesResult();
    when(cognito.adminUpdateUserAttributes(request)).thenReturn(result);
    return request;
  }

  public void setCreateUserResult(AdminCreateUserRequest request, String newId) {

    UserType newUser = new UserType();
    newUser.setUsername(newId);
    newUser.setEnabled(true);
    newUser.setUserStatus("FORCE_CHANGE_PASSWORD");
    newUser.withAttributes(request.getUserAttributes());

    AdminCreateUserResult result = new AdminCreateUserResult().withUser(newUser);
    when(cognito.adminCreateUser(request)).thenReturn(result);
  }

  private void setSearchByRacfidRequestAndResult(TestUser testUser) {
    setSearchByRacfidRequestAndResult(testUser.getRacfId(), userType(testUser));
  }

  public void setSearchByRacfidRequestAndResult(String racfid,
      UserType... responseUsers) {

    ListUsersRequest request =
        composeListUsersRequest(
            composeToGetFirstPageByAttribute(RACFID_STANDARD, racfid));

    ListUsersResult result = new ListUsersResult().withUsers(responseUsers);

    when(cognito.listUsers(request)).thenReturn(result);
  }

  private void setSearchByRacfidRequestAndReturnResults(TestUser testUser1,
      TestUser testUser2) {

    ListUsersRequest request =
        composeListUsersRequest(
            composeToGetFirstPageByAttribute(RACFID_STANDARD, testUser1.getRacfId()));

    ListUsersResult result = new ListUsersResult()
        .withUsers(userType(testUser1), userType(testUser2));

    when(cognito.listUsers(request)).thenReturn(result);
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
    private String roles;
    private String racfId;
    private String officeId;
    private String phoneNumber;
    private String phoneExtension;
    private String failedLoginsCount;

    TestUser(
        String id,
        Boolean enabled,
        String status,
        Date userCreateDate,
        Date lastModifiedDate,
        String email,
        String firstName,
        String lastName,
        String county,
        String permissions,
        String roles,
        String racfId,
        String officeId,
        String phoneNumber,
        String phneExtension,
        String failedLoginsCount) {
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
      this.roles = roles;
      this.racfId = racfId;
      this.officeId = officeId;
      this.phoneNumber = phoneNumber;
      this.phoneExtension = phneExtension;
      this.failedLoginsCount = failedLoginsCount;
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

    public String getRoles() {
      return roles;
    }

    public String getRacfId() {
      return racfId;
    }

    public String getOfficeId() {
      return officeId;
    }

    public String getPhoneNumber() {
      return phoneNumber;
    }

    public String getPhoneExtension() {
      return phoneExtension;
    }

    public String getFailedLoginsCount() {
      return failedLoginsCount;
    }
  }
}