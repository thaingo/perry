package gov.ca.cwds.idm.service.cognito.dto;

import gov.ca.cwds.idm.service.cognito.StandardUserAttribute;

@SuppressWarnings({"fb-contrib:OCP_OVERLY_CONCRETE_PARAMETER"})
public class CognitoUsersSearchCriteria {

  private Integer pageSize;
  private String paginationToken;
  private String attrName;
  private String attrValue;

  public CognitoUsersSearchCriteria(){}

  public CognitoUsersSearchCriteria(CognitoUsersSearchCriteria another) {
    this.pageSize = another.pageSize;
    this.paginationToken = another.paginationToken;
    this.attrName = another.attrName;
    this.attrValue = another.attrValue;
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

  public String getSearchAttrName() {
    return attrName;
  }

  public String getSearchAttrValue() {
    return attrValue;
  }

  public void setSearchAttr(StandardUserAttribute attr, String attrValue) {
    this.attrName = attr.getName();
    this.attrValue = attrValue;
  }

  public static final class SearchParameterBuilder {
    private Integer pageSize;
    private String paginationToken;
    private StandardUserAttribute attr;
    private String attrValue;

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

    public SearchParameterBuilder withSearchAttr(StandardUserAttribute attr, String attrValue) {
      this.attr = attr;
      this.attrValue = attrValue;
      return this;
    }

    public CognitoUsersSearchCriteria build() {
      CognitoUsersSearchCriteria searchCriteria = new CognitoUsersSearchCriteria();
      searchCriteria.setPageSize(pageSize);
      searchCriteria.setPaginationToken(paginationToken);
      if(attr != null) {
        searchCriteria.setSearchAttr(attr, attrValue);
      }
      return searchCriteria;
    }
  }
}
