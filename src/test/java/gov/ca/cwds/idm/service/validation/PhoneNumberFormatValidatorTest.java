package gov.ca.cwds.idm.service.validation;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class PhoneNumberFormatValidatorTest {

  @Test
  public void testIsValid() {
    assertFalse(PhoneNumberFormatValidator.isValid("+0123456789"));
    assertFalse(PhoneNumberFormatValidator.isValid("012 345 6789"));
    assertFalse(PhoneNumberFormatValidator.isValid("012-345-6789"));
    assertFalse(PhoneNumberFormatValidator.isValid("(012) 345-6789"));
    assertFalse(PhoneNumberFormatValidator.isValid(null));
    assertFalse(PhoneNumberFormatValidator.isValid(""));
    assertFalse(PhoneNumberFormatValidator.isValid("  "));
    assertFalse(PhoneNumberFormatValidator.isValid("0123456789"));
    assertFalse(PhoneNumberFormatValidator.isValid("0"));

    assertTrue(PhoneNumberFormatValidator.isValid("1234567890"));
    assertTrue(PhoneNumberFormatValidator.isValid("45678"));
    assertTrue(PhoneNumberFormatValidator.isValid("1"));
  }
}
