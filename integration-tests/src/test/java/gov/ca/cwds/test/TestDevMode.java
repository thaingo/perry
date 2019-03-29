package gov.ca.cwds.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

@RunWith(SerenityParameterizedRunner.class)
@Concurrent
public class TestDevMode {

  private WebDriver driver;
  private final TestDataBean testDataBean;
  @Steps
  private LoginSteps loginSteps;

  public TestDevMode(TestDataBean testDataBean) {
    this.testDataBean = testDataBean;
  }

  @BeforeClass
  public static void setupClass() {
    WebDriverManager.chromedriver().setup();
  }

  @Before
  public void init() throws MalformedURLException {
    if (StringUtils.isEmpty(testDataBean.getGridUrl())) {
      System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER"));
      ChromeOptions chromeOptions = new ChromeOptions();
      chromeOptions.addArguments("--headless");
      chromeOptions.addArguments("--no-sandbox");
      driver = new ChromeDriver(chromeOptions);
    } else {
      driver = new RemoteWebDriver(new URL(testDataBean.getGridUrl()), DesiredCapabilities.chrome());
    }
    loginSteps.setDriver(driver);
  }

  @After
  public void teardown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @TestData
  public static Collection<Object[]> testData() {
    int threadsCount = Integer.parseInt(System.getProperty("perry.threads.count", "1"));
    return IntStream.range(0, threadsCount).boxed().map(i -> {
      Object[] item = new TestDataBean[1];
      item[0] = new TestDataBean();
      return item;
    }).collect(Collectors.toList());
  }

  @Test
  public void devModeTest() throws Exception {
    String json =
        IOUtils.toString(getClass().getResourceAsStream("/dev.json"), Charset
        .defaultCharset());
    loginSteps.goToPerryLoginUrl(testDataBean.getUrl() + "/authn/login?callback=" + testDataBean.getUrl() + "/demo-sp.html");
    loginSteps.isElementPresent("username");
    loginSteps.type("username", json);
    loginSteps.click("submitBtn");
    String accessCode = loginSteps.waitForAccessCodeParameter();
    String perryToken = loginSteps.mapAccessCode(testDataBean.getUrl() + "/authn/token?accessCode=" + accessCode);
    for (int i = 0; i < Integer.parseInt(testDataBean.getValidateRepeatCount()); i++) {
      String jsonToken =
          loginSteps.validateToken(testDataBean.getUrl() + "/authn/validate?token=" + perryToken);
      loginSteps.validateTokenContent(json, jsonToken);
    }
  }
}


