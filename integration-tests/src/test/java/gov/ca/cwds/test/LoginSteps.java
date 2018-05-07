package gov.ca.cwds.test;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import com.jayway.restassured.RestAssured;
import net.thucydides.core.annotations.Step;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.skyscreamer.jsonassert.JSONAssert;


import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;


public class LoginSteps {

  private WebDriver driver;

  public void setDriver(WebDriver driver) {
    this.driver = driver;
  }

  @Step
  public void goToPerryLoginUrl(String url) {
    driver.get(url);
  }

  @Step
  public void isElementPresent(String id) {
    new WebDriverWait(driver, 5).until(presenceOfElementLocated(By.id(id)));
  }

  @Step
  public void type(String id, String username) {
    find(id).sendKeys(username);
  }

  private WebElement find(String selector) {
    try {
      return driver.findElement(By.id(selector));
    } catch (NoSuchElementException e) {
      return driver.findElement(By.name(selector));
    }
  }

  @Step
  public void click(String id) {
    find(id).click();
  }

  @Step
  public String waitForAccessCodeParameter() {
    new WebDriverWait(driver, 5).until(urlContains("accessCode"));
    String accessCode = URLEncodedUtils.parse(driver.getCurrentUrl(), Charset.defaultCharset())
        .stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue)).get("accessCode");
    driver.close();
    return accessCode;
  }

  @Step
  public String mapAccessCode(String url) {
    return RestAssured.get(url).asString();
  }

  @Step
  public String validateToken(String url) {
    return RestAssured.get(url).asString();
  }

  @Step
  public void validateTokenContent(String input, String token) throws JSONException {
    JSONAssert.assertEquals(input, token, false);
  }

}
