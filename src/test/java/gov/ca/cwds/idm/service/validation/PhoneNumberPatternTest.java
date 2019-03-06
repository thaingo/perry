package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.idm.service.validation.ValidationServiceImpl.PHONE_PATTERN_VALIDATOR;

import org.junit.Test;

public class PhoneNumberPatternTest extends BasePatternTest {

  public PhoneNumberPatternTest() {
    super(PHONE_PATTERN_VALIDATOR);
  }

  @Test
  public void testIsValid() {

    invalid("+0123456789");
    invalid("012 345 6789");
    invalid("012-345-6789");
    invalid("(012) 345-6789");
    invalid("0123456789");
    invalid("45678");
    invalid("0");
    invalid("1");

    valid("1234567890");
  }
}
