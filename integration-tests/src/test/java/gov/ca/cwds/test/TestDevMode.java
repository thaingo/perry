package gov.ca.cwds.test;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityParameterizedRunner.class)
@Concurrent
public class TestDevMode {

  @Managed(driver = "chrome", uniqueSession = true)
  private WebDriver driver;
  private String basePerryUrl;

  @Steps
  private LoginSteps loginSteps;

  @Before
  public void init() {
    loginSteps.setDriver(driver);
    basePerryUrl = System.getProperty("perry.url", "http://localhost:8080/perry");
  }

  @TestData
  public static Collection<Object[]> testData() {
    //TODO: separate data per thread
    int threadsCount = Integer.valueOf(System.getProperty("perry.threads.count", "4"));
    return IntStream.range(0, threadsCount).boxed().map(i -> {
      Object[] item = new Integer[1];
      item[0] = i;
      return item;
    }).collect(Collectors.toList());
  }

  public TestDevMode(int number) {
    //TODO: separate data per thread
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
    loginSteps.validateTokenContent(inputJson, jsonToken);
  }
}


