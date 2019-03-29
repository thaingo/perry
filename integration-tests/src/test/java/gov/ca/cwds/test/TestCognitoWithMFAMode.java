package gov.ca.cwds.test;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;

@RunWith(SerenityParameterizedRunner.class)
@Concurrent
public class TestCognitoWithMFAMode {

  private static final Logger LOG = LoggerFactory.getLogger(TestCognitoWithMFAMode.class);

  private WebDriver driver;
  private final TestDataBean testDataBean;
  private ChromeDriverService service;

  @Steps
  private LoginSteps loginSteps;

  public TestCognitoWithMFAMode(TestDataBean testDataBean) {
    this.testDataBean = testDataBean;
  }

  @TestData
  public static Collection<Object[]> testData() {
    // TODO: separate data per thread
    int threadsCount = Integer.parseInt(System.getProperty("perry.threads.count", "1"));
    return IntStream.range(0, threadsCount).boxed().map(i -> {
      Object[] item = new TestDataBean[1];
      item[0] = new TestDataBean();
      return item;
    }).collect(Collectors.toList());
  }

  @BeforeClass
  public static void setupClass() {
    WebDriverManager.chromedriver().setup();
  }

  @Before
  public void init() throws MalformedURLException {
    System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER"));
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.addArguments("--headless");
    chromeOptions.addArguments("--no-sandbox");
    driver = new ChromeDriver(chromeOptions);
    loginSteps.setDriver(driver);
  }

  @After
  public void teardown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void testCognitoMode() throws Exception {
    loginSteps.goToPerryLoginUrl(
        testDataBean.getUrl() + "/authn/login?callback=" + testDataBean.getUrl() + "/demo-sp.html");
    loginSteps.isElementPresent("email");
    loginSteps.type("email", System.getenv("SMOKE_TEST_USER"));
    loginSteps.type("password", System.getenv("SMOKE_TEST_PASSWORD"));
    loginSteps.isElementPresent("submit");
    loginSteps.click("submit");
    loginSteps.isElementPresent("code");
    loginSteps.type("code", System.getenv("SMOKE_VERIFICATION_CODE"));
    loginSteps.isElementPresent("validateButton");
    loginSteps.click("validateButton");
    String accessCode = loginSteps.waitForAccessCodeParameter();
    String perryToken =
        loginSteps.mapAccessCode(testDataBean.getUrl() + "/authn/token?accessCode=" + accessCode);
    for (int i = 0; i < Integer.parseInt(testDataBean.getValidateRepeatCount()); i++) {
      String jsonToken =
          loginSteps.validateToken(testDataBean.getUrl() + "/authn/validate?token=" + perryToken);
      loginSteps.validateTokenContent(testDataBean.getJson(), jsonToken);
    }
  }

}
