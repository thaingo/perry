package gov.ca.cwds.idm.service.diff.builder;

import gov.ca.cwds.idm.service.diff.UserAttributeDiff;

/**
 * Created by Alexander Serbin on 1/15/2019
 */
public interface UserAttributeDiffBuilder<T> {

  /**
   * Determines if there is any difference between old and new attribute values.
   * @return
   */
  boolean doesDiffExist();

  /**
   * Builds diffs with AttributeType, old and new attribute value.
   */
  UserAttributeDiff<T> buildDiff();

}