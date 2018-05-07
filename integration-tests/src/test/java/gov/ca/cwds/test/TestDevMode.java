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
public class TestDevMode {

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
  public void devModeTest() throws Exception {
    String inputJson = IOUtils.toString(getClass().getResourceAsStream("/dev.json"), Charset.defaultCharset());
    loginSteps.goToPerryLoginUrl(basePerryUrl + "/authn/login?callback=/perry/demo-sp.html");
    loginSteps.isElementPresent("username");
    loginSteps.type("username", inputJson);
    loginSteps.click("submitBtn");
    String accessCode = loginSteps.waitForAccessCodeParameter();
    String perryToken = loginSteps.mapAccessCode(basePerryUrl + "/authn/token?accessCode=" + accessCode);
    String jsonToken = loginSteps.validateToken(basePerryUrl + "/authn/validate?token=" + perryToken);
    loginSteps.compareInputAndToken(inputJson, jsonToken);
  }
}


