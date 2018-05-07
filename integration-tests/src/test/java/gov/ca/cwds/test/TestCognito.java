package gov.ca.cwds.test;

import java.nio.charset.Charset;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityRunner.class)
public class TestCognito {

  @Managed(driver = "chrome")
  private WebDriver driver;
  private String basePerryUrl;

  @Steps
  private LoginSteps loginSteps;

  @Before
  public void init() {
    loginSteps.setDriver(driver);
    basePerryUrl = System.getProperty("perry.url", "http://localhost:8080/perry");
  }

  @Test
  public void testCognitoMode() throws Exception {
    loginSteps.goToPerryLoginUrl(basePerryUrl + "/authn/login?callback=/perry/demo-sp.html");
    loginSteps.isElementPresent("username");
    loginSteps.type("username", "perry");
    loginSteps.type("password", "Password123!");
    loginSteps.click("signInSubmitButton");
    String accessCode = loginSteps.waitForAccessCodeParameter();
    String perryToken = loginSteps.mapAccessCode(basePerryUrl + "/authn/token?accessCode=" + accessCode);
    String jsonToken = loginSteps.validateToken(basePerryUrl + "/authn/validate?token=" + perryToken);
    String expectedJsonToken = IOUtils.toString(getClass().getResourceAsStream("/cognito.json"), Charset.defaultCharset());
    loginSteps.validateTokenContent(expectedJsonToken, jsonToken);
  }
}