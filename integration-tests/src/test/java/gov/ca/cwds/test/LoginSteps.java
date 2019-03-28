package gov.ca.cwds.test;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.skyscreamer.jsonassert.JSONAssert;

import com.jayway.restassured.RestAssured;

import net.thucydides.core.annotations.Step;


public class LoginSteps {


  public WebDriver driver;

  public void setDriver(WebDriver driver) {
    this.driver = driver;
  }

  @Step
  public void goToPerryLoginUrl(String url) {
    driver.get(url);
  }

  @Step
  public void isElementPresent(String id) {
    new WebDriverWait(driver, 15).until(presenceOfElementLocated(By.id(id)));
  }

  @Step
  public void isElementPresentXpath(String xpath) {
    new WebDriverWait(driver, 5).until(presenceOfElementLocated(By.xpath(xpath)));
  }

  @Step
  public void type(String id, String username) {
    find(id).sendKeys(username);
  }

  @Step
  public void typeXpath(String xpath, String text) {
    findXpath(xpath).sendKeys(text);
  }

  private WebElement findXpath(String xpath) {
    try {
      return driver.findElement(By.xpath(xpath));
    } catch (NoSuchElementException e) {
      return driver.findElement(By.xpath(xpath));
    }
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
  public void clickXpath(String xpath) {
    findXpath(xpath).submit();
  }

  @Step
  public String waitForAccessCodeParameter() throws Exception {
    new WebDriverWait(driver, 5).until(urlContains("accessCode"));
    String accessCode =
        URLEncodedUtils.parse(new URI(driver.getCurrentUrl()), Charset.defaultCharset()).stream()
            .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue))
            .get("accessCode");
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
