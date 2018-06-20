package gov.ca.cwds.idm.dto;

public class UsersSearchParameter {
  private String userCounty;
  private String lastName;
  private Integer pageSize;
  private String email;

  public String getUserCounty() {
    return userCounty;
  }

  public void setUserCounty(String userCounty) {
    this.userCounty = userCounty;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public static final class SearchParameterBuilder {
    private String userCounty;
    private String lastName;
    private Integer pageSize;
    private String email;

    private SearchParameterBuilder() {}

    public static SearchParameterBuilder aSearchParameters() {
      return new SearchParameterBuilder();
    }

    public SearchParameterBuilder withUserCounty(String userCounty) {
      this.userCounty = userCounty;
      return this;
    }

    public SearchParameterBuilder withLastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public SearchParameterBuilder withPageSize(Integer pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public SearchParameterBuilder withEmail(String email) {
      this.email = email;
      return this;
    }

    public UsersSearchParameter build() {
      UsersSearchParameter usersSearchParameter = new UsersSearchParameter();
      usersSearchParameter.setUserCounty(userCounty);
      usersSearchParameter.setLastName(lastName);
      usersSearchParameter.setPageSize(pageSize);
      usersSearchParameter.setEmail(email);
      return usersSearchParameter;
    }
  }
}
