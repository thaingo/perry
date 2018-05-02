package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertStrict;

import gov.ca.cwds.PerryApplication;
import gov.ca.cwds.idm.service.PermissionService;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PerryApplication.class, properties = "spring.jpa.hibernate.ddl-auto=none")
@WebAppConfiguration
@ActiveProfiles("dev")
public class IdmResourceTest {

  private static final MediaType CONTENT_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @MockBean
  private PermissionService permissionService;

  @Before
  public void before() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  public void testGetPermissions() throws Exception {

    List<String> permissions = Arrays.asList(
        "cals-core-county",
        "cals-core-user",
        "development-not-in-use",
        "intake-core-county",
        "intake-core-user"
    );

    BDDMockito.given(permissionService.getPermissionNames()).willReturn(permissions);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/idm/permissions/"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE))
        .andReturn();

    assertStrict(result, "fixtures/idm/permissions/valid.json");
  }
}
