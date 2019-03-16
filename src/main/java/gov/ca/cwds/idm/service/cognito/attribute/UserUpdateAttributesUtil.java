package gov.ca.cwds.idm.service.cognito.attribute;

import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL;
import static gov.ca.cwds.idm.service.cognito.attribute.StandardUserAttribute.EMAIL_VERIFIED;
import static gov.ca.cwds.idm.service.cognito.util.CognitoUtils.TRUE_VALUE;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
import gov.ca.cwds.idm.service.diff.StringDiff;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserUpdateAttributesUtil {

  private UserUpdateAttributesUtil() {
  }

  public static List<AttributeType> buildUpdatedAttributesList(UpdateDifference updateDifference) {
    List<AttributeType> attrs = new ArrayList<>();
    addEmailAttributes(updateDifference.getEmailDiff(), attrs);
    return attrs;
  }

  private static void addEmailAttributes(Optional<StringDiff> optEmailDiff, List<AttributeType> attrs) {
    optEmailDiff.ifPresent(
        diff -> {
          addStringAttribute(EMAIL, diff.getNewValue(), attrs);
          addStringAttribute(EMAIL_VERIFIED, TRUE_VALUE, attrs);
        }
    );
  }

  private static void addStringAttribute(UserAttribute userAttribute, String value,
      List<AttributeType> attrs) {
    attrs.add(new AttributeType().withName(userAttribute.getName()).withValue(value));
  }
}
