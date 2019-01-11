package gov.ca.cwds.idm.service.validation;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class PhoneNumberValidatorTest {

  @Test
  public void testIsValid() {
    assertFalse(PhoneNumberValidator.isValid(null));
    assertFalse(PhoneNumberValidator.isValid(""));
    assertFalse(PhoneNumberValidator.isValid("  "));
    assertFalse(PhoneNumberValidator.isValid("+0123456789"));
    assertFalse(PhoneNumberValidator.isValid("012 345 6789"));
    assertFalse(PhoneNumberValidator.isValid("012-345-6789"));
    assertFalse(PhoneNumberValidator.isValid("(012) 345-6789"));

    assertTrue(PhoneNumberValidator.isValid("0123456789"));
  }
}
