package gov.ca.cwds.test;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.jayway.restassured.RestAssured;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;

@RunWith(SerenityRunner.class)
public class TestDevMode {

  @Managed(driver = "chrome")
  WebDriver driver;

  @Test
  public void superSimpleDevModeTest()  {
    driver.get("http://localhost:8080/perry/authn/login?callback=/perry/demo-sp.html");

    new WebDriverWait(driver, 5).until(presenceOfElementLocated(By.id("username")));
    driver.findElement(By.id("username")).sendKeys("{ \"user\": \"RACFID\", \"staffId\": \"0X5\", \"roles\": [ \"Supervisor\" ], \"county_code\": \"56\", \"county_cws_code\": \"1123\", \"county_name\": \"Ventura\", \"privileges\": [ \"CWS Case Management System\", \"Resource Management\", \"Resource Mgmt Placement Facility Maint\", \"Sealed\", \"Sensitive Persons\" ] }");
    driver.findElement(By.id("submitBtn")).click();
    new WebDriverWait(driver, 5).until(urlContains("demo-sp.html"));

   Map<String, String> params = URLEncodedUtils.parse(URI.create(driver.getCurrentUrl()), Charset.defaultCharset())
        .stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));

    String token = RestAssured.get("http://localhost:8080/perry/authn/token?accessCode={accessCode}", params).asString();

    params.clear();
    params.put("token", token);
    String json = RestAssured.get("http://localhost:8080/perry/authn/validate?token={token}", params).asString();
    System.out.println(json);


  }
}