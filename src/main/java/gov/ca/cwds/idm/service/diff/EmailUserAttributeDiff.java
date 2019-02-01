package gov.ca.cwds.idm.service.diff;

import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;

/**
 * Created by Alexander Serbin on 1/16/2019
 */
public final class EmailUserAttributeDiff extends StringUserAttributeDiff {

  public EmailUserAttributeDiff(String oldValue, String newValue) {
    super(EMAIL, oldValue, newValue);
  }
}
