package gov.ca.cwds.idm.dto;

import gov.ca.cwds.idm.service.cognito.StandardUserAttribute;
import java.util.Set;

public class UsersSearchCriteria {

  private final StandardUserAttribute searchAttr;

  private final Set<String> values;

  public UsersSearchCriteria(StandardUserAttribute searchAttr, Set<String> values) {
    this.searchAttr = searchAttr;
    this.values = values;
  }

  public StandardUserAttribute getSearchAttr() {
    return searchAttr;
  }

  public Set<String> getValues() {
    return values;
  }
}
