package gov.ca.cwds.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

@RunWith(SerenityParameterizedRunner.class)
@Concurrent
public class TestDevMode {

  private WebDriver driver;
  final private TestDataBean testDataBean;
  @Steps
  private LoginSteps loginSteps;

  @BeforeClass
  public static void setupClass() {
    WebDriverManager.chromedriver().setup();
  }

  @Before
  public void init() {
    driver = new ChromeDriver();
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
    int threadsCount = Integer.valueOf(System.getProperty("perry.threads.count", "1"));
    return IntStream.range(0, threadsCount).boxed().map(i -> {
      Object[] item = new TestDataBean[1];
      item[0] = new TestDataBean();
      return item;
    }).collect(Collectors.toList());
  }

  public TestDevMode(TestDataBean testDataBean) {
    this.testDataBean = testDataBean;
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
    String jsonToken = loginSteps.validateToken(testDataBean.getUrl() + "/authn/validate?token=" + perryToken);
    loginSteps.validateTokenContent(json, jsonToken);
  }
}


