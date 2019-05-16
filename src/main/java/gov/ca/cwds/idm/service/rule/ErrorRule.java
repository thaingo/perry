package gov.ca.cwds.idm.service.rule;

import gov.ca.cwds.idm.exception.IdmException;

public interface ErrorRule {

  boolean hasError();

  IdmException createException();

  default void check(){
    if(hasError()) {
      throw createException();
    }
  }
}
