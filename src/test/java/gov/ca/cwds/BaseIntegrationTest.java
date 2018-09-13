package gov.ca.cwds;

import static gov.ca.cwds.Constants.H2_DRIVER_CLASS_NAME;
import static gov.ca.cwds.util.LiquibaseUtils.createCmsDatabase;
import static gov.ca.cwds.util.LiquibaseUtils.createTokenStoreDatabase;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class BaseIntegrationTest {

  protected MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @BeforeClass
  public static void prepareDatabases() throws Exception {
    Class.forName(H2_DRIVER_CLASS_NAME);
    createTokenStoreDatabase();
    createCmsDatabase();
  }

  @Before
  public void initMockMvc() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity()).build();
  }

}
