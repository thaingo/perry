package gov.ca.cwds.idm.dto;

public class UsersSearchParameter {
  private String userCounty;
  private String lastName;
  private Integer pageSize;

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

  public static final class SearchParameterBuilder {
    private String userCounty;
    private String lastName;
    private Integer pageSize;

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

    public UsersSearchParameter build() {
      UsersSearchParameter searchParameters = new UsersSearchParameter();
      searchParameters.setUserCounty(userCounty);
      searchParameters.setLastName(lastName);
      searchParameters.setPageSize(pageSize);
      return searchParameters;
    }
  }
}
