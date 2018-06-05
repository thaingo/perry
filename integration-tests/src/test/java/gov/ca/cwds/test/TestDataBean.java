package gov.ca.cwds.test;

public class TestDataBean {
  private String username;
  private String password;
  private String url;
  private String json;
  private String gridUrl;

  public TestDataBean() {
    username = System.getProperty("perry.username");
    password = System.getProperty("perry.password");
    url = System.getProperty("perry.url");
    json = System.getProperty("perry.json");
    gridUrl = System.getProperty("selenium.grid.url");
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getUrl() {
    return url;
  }

  public String getJson() {
    return json;
  }

  public String getGridUrl() {
    return gridUrl;
  }
}
