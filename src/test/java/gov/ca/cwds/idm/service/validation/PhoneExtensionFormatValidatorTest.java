package gov.ca.cwds.idm.service.validation;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class PhoneExtensionFormatValidatorTest {

  @Test
  public void testIsValid() {
    assertFalse(PhoneExtensionFormatValidator.isValid("+012"));
    assertFalse(PhoneExtensionFormatValidator.isValid("1 5"));
    assertFalse(PhoneExtensionFormatValidator.isValid("2-3"));
    assertFalse(PhoneExtensionFormatValidator.isValid("(012)"));
    assertFalse(PhoneExtensionFormatValidator.isValid("012345678"));

    assertTrue(PhoneExtensionFormatValidator.isValid(null));
    assertTrue(PhoneExtensionFormatValidator.isValid(""));
    assertTrue(PhoneExtensionFormatValidator.isValid("  "));
    assertTrue(PhoneExtensionFormatValidator.isValid("1234567"));
    assertTrue(PhoneExtensionFormatValidator.isValid("456"));
    assertTrue(PhoneExtensionFormatValidator.isValid("1"));
    assertTrue(PhoneExtensionFormatValidator.isValid("0"));
  }
}
