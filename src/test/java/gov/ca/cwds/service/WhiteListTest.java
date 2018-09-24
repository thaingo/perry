package gov.ca.cwds.service;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.rest.api.domain.PerryException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dmitry.rudenko on 10/2/2017.
 */
public class WhiteListTest {

  @Test(expected = PerryException.class)
  public void testEnabledInvalidUrl() {
    WhiteList whiteList = new WhiteList();
    PerryProperties perryProperties = new PerryProperties();
    perryProperties.setWhiteList("url1 url2");
    whiteList.configuration = perryProperties;
    whiteList.validate("", "url3");
  }

  @Test
  public void testEnabledValidUrl() {
    WhiteList whiteList = new WhiteList();
    PerryProperties perryProperties = new PerryProperties();
    perryProperties.setWhiteList("url1 url2");
    whiteList.configuration = perryProperties;
    whiteList.validate("", "url1");
  }

  @Test
  public void testDisabled() {
    WhiteList whiteList = new WhiteList();
    PerryProperties perryProperties = new PerryProperties();
    perryProperties.setWhiteList("*");
    whiteList.configuration = perryProperties;
    whiteList.validate("", "url3");
    whiteList.validate("", "url3/path");
  }

  @Test
  public void testWildcards() {
    WhiteList whiteList = new WhiteList();
    PerryProperties perryProperties = new PerryProperties();
    whiteList.configuration = perryProperties;

    perryProperties.setWhiteList("**/test_1*");
    whiteList.validate("", "http://test_12");

    perryProperties.setWhiteList("http://tests/path*");
    whiteList.validate("", "http://tests/path_1");

    perryProperties.setWhiteList("http://tests2/*/inside");
    whiteList.validate("", "http://tests2/path/inside");
    whiteList.validate("", "http://tests2/path_1/inside");

    perryProperties.setWhiteList("http://tests2/**/*.*");
    whiteList.validate("", "http://tests2/path/page.html");
    whiteList.validate("", "http://tests2/path/page.js");

    perryProperties.setWhiteList("http://tests3/**");
    whiteList.validate("", "http://tests3/path/inside?param=value");
    whiteList.validate("", "http://tests3/path/inside/more?param=value");

    try {
      perryProperties.setWhiteList("http://tests3/**");
      whiteList.validate("", "http://tests_other/path");
      Assert.fail();
    } catch (PerryException pe) {
      // Do nothing
    }
  }
}
