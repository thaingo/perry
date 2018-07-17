package gov.ca.cwds.idm.service.cognito.dto;

public class CognitoUsersSearchCriteria {

  private Integer pageSize;
  private String paginationToken;
  private String email;
  private String rafcid;

  public CognitoUsersSearchCriteria(){}

  public CognitoUsersSearchCriteria(CognitoUsersSearchCriteria another) {
    this.pageSize = another.pageSize;
    this.paginationToken = another.paginationToken;
    this.email = another.email;
    this.rafcid = another.rafcid;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public String getPaginationToken() {
    return paginationToken;
  }

  public void setPaginationToken(String paginationToken) {
    this.paginationToken = paginationToken;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRafcid() {
    return rafcid;
  }

  public void setRafcid(String rafcid) {
    this.rafcid = rafcid;
  }

  public static final class SearchParameterBuilder {
    private Integer pageSize;
    private String paginationToken;
    private String email;
    private String rafcid;

    private SearchParameterBuilder() {}

    public static SearchParameterBuilder aSearchParameters() {
      return new SearchParameterBuilder();
    }

    public SearchParameterBuilder withPageSize(Integer pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public SearchParameterBuilder withPaginationToken(String paginationToken) {
      this.paginationToken = paginationToken;
      return this;
    }

    public SearchParameterBuilder withEmail(String email) {
      this.email = email;
      return this;
    }
    public SearchParameterBuilder withRacfid(String rafcid) {
      this.rafcid = rafcid;
      return this;
    }

    public CognitoUsersSearchCriteria build() {
      CognitoUsersSearchCriteria usersSearchParameter = new CognitoUsersSearchCriteria();
      usersSearchParameter.setPageSize(pageSize);
      usersSearchParameter.setPaginationToken(paginationToken);
      usersSearchParameter.setEmail(email);
      usersSearchParameter.setRafcid(rafcid);
      return usersSearchParameter;
    }
  }
}
