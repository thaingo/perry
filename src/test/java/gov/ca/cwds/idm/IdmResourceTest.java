package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;
import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertStrict;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.idm.service.CognitoServiceFacade;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import javax.annotation.PostConstruct;
import org.junit.Before;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles({"dev", "idm"})
@TestPropertySource(locations = "classpath:test.properties")
public class IdmResourceTest extends BaseTokenStoreLiquibaseTest {

  private static final MediaType CONTENT_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

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
  public void testGetUser() throws Exception {

    authenticate("Yolo", "CARES admin");

    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get("/idm/users/2be3221f-8c2f-4386-8a95-a68f0282efb0"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
        .andReturn();

    assertNonStrict(result, "fixtures/idm/get-user/valid.json");
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
      if(beanName.equals("cognitoServiceFacade")){
        return new TestCognitoServiceFacade();
      } else {
        return bean;
      }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      return bean;
    }
  }

  static class TestCognitoServiceFacade extends CognitoServiceFacade {
    @PostConstruct
    @Override
    public void init() {
      AWSCognitoIdentityProvider identityProvider = mock(AWSCognitoIdentityProvider.class);

      CognitoProperties properties = new CognitoProperties();
      properties.setIamAccessKeyId("iamAccessKeyId");
      properties.setIamSecretKey("iamSecretKey");
      properties.setUserpool("userpool");
      properties.setRegion("us-east-2");

      setProperties(properties);
      setIdentityProvider(identityProvider);

      AdminGetUserResult mockResult = new AdminGetUserResult();
      mockResult.setUsername("2be3221f-8c2f-4386-8a95-a68f0282efb0");
      mockResult.setEnabled(Boolean.TRUE);
      mockResult.setUserStatus("FORCE_CHANGE_PASSWORD");
      mockResult.setUserCreateDate(date(2018, 5, 4));
      mockResult.setUserLastModifiedDate(date(2018, 5, 30));

      mockResult.withUserAttributes(
          attr("email", "donzano@gmail.com"),
          attr("given_name", "Don"),
          attr("family_name", "Manzano"),
          attr("custom:County", "Yolo"),
          attr("custom:RACFID", "SMITHBR"),
          attr("custom:permission", "RFA-rollout:Snapshot-rollout:")
      );

      when(identityProvider.adminGetUser(any(AdminGetUserRequest.class)))
          .thenReturn(mockResult);
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
}
