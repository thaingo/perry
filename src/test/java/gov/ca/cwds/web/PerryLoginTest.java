package gov.ca.cwds.web;

import static gov.ca.cwds.idm.BaseLiquibaseTest.CMS_STORE_URL;
import static gov.ca.cwds.idm.BaseLiquibaseTest.TOKEN_STORE_URL;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import gov.ca.cwds.idm.BaseLiquibaseTest;
import gov.ca.cwds.service.sso.custom.form.FormService;
import io.dropwizard.testing.FixtureHelpers;
import javax.servlet.http.Cookie;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles({"dev"})
@SpringBootTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "perry.identityManager.idmMapping=config/idm.groovy",
    "perry.tokenStore.datasource.url=" + TOKEN_STORE_URL,
    "spring.datasource.url=" + CMS_STORE_URL,
    "perry.whiteList=" + "/demo-sp.html"
})
public class PerryLoginTest extends BaseLiquibaseTest {

  @Autowired
  private FormService formService;

  @Autowired
  private FilterChainProxy filterChain;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Before
  public void before() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(filterChain).apply(springSecurity()).build();
  }

  @Test
  public void whenLoginJsonProvided_thenReturnOk() throws Exception {
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/authn/login?callback=/demo-sp.html"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

    Cookie[] cookies = result.getResponse().getCookies();

    System.out.println("getRedirectedUrl: [" + result.getResponse().getRedirectedUrl() + "]");
    System.out.println("getForwardedUrl: [" + result.getResponse().getForwardedUrl() + "]");
    System.out.println("getContentType: [" + result.getResponse().getContentType() + "]");
    System.out.println("getHeaderNames: [" + result.getResponse().getHeaderNames() + "]");
    System.out.println("getIncludedUrls: [" + result.getResponse().getIncludedUrls() + "]");
    System.out.println("getContentAsString: [" + result.getResponse().getContentAsString() + "]");
    System.out.println("getCookies: [" + result.getResponse().getCookies().length + "]");

    result = mockMvc.perform(MockMvcRequestBuilders.post("/login").cookie(cookies)
        .param("username", FixtureHelpers.fixture("fixtures/mfa/auth.json"))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

    System.out.println("getRedirectedUrl: [" + result.getResponse().getRedirectedUrl() + "]");
    System.out.println("getForwardedUrl: [" + result.getResponse().getForwardedUrl() + "]");
    System.out.println("getContentType: [" + result.getResponse().getContentType() + "]");
    System.out.println("getHeaderNames: [" + result.getResponse().getHeaderNames() + "]");
    System.out.println("getIncludedUrls: [" + result.getResponse().getIncludedUrls() + "]");
    System.out.println("getContentAsString: [" + result.getResponse().getContentAsString() + "]");
    System.out.println("getCookies: [" + result.getResponse().getCookies() + "]");

    cookies = result.getResponse().getCookies();
    Assert.assertEquals(cookies.length, 2);
  }


}
