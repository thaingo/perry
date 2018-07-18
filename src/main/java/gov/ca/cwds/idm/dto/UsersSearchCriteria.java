package gov.ca.cwds.idm.dto;

import gov.ca.cwds.idm.service.cognito.StandardUserAttribute;
import java.util.HashSet;
import java.util.Set;

public class UsersSearchCriteria {

  private StandardUserAttribute searchAttr;

  private Set<String> values = new HashSet<>();

  public StandardUserAttribute getSearchAttr() {
    return searchAttr;
  }

  public void setSearchAttr(StandardUserAttribute searchAttr) {
    this.searchAttr = searchAttr;
  }

  public Set<String> getValues() {
    return values;
  }

  public void setValues(Set<String> values) {
    this.values = values;
  }
}
