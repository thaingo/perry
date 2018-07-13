package gov.ca.cwds.idm.dto;

public class UsersSearchParameter {
  private Integer pageSize;
  private String email;

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
    private Integer pageSize;
    private String email;

    private SearchParameterBuilder() {}

    public static SearchParameterBuilder aSearchParameters() {
      return new SearchParameterBuilder();
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
      usersSearchParameter.setPageSize(pageSize);
      usersSearchParameter.setEmail(email);
      return usersSearchParameter;
    }
  }
}
