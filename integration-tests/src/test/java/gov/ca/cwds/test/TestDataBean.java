package gov.ca.cwds.test;

public class TestDataBean {
  private String username;
  private String password;
  private String url;
  private String json;

  public TestDataBean() {
    username = System.getProperty("perry.username");
    password = System.getProperty("perry.password");
    url = System.getProperty("perry.url");
    json = System.getProperty("perry.json");
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
}
