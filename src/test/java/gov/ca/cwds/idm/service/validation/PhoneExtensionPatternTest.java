package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.idm.service.validation.ValidationServiceImpl.PHONE_EXTENSION_PATTERN_VALIDATOR;

import org.junit.Test;

public class PhoneExtensionPatternTest extends BasePatternTest {

  public PhoneExtensionPatternTest() {
    super(PHONE_EXTENSION_PATTERN_VALIDATOR);
  }

  @Test
  public void testIsValid() {
    invalid("+012");
    invalid("1 5");
    invalid("2-3");
    invalid("(012)");
    invalid("012345678");

    valid("1234567");
    valid("456");
    valid("1");
    valid("0");
  }
}
