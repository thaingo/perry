package gov.ca.cwds.idm.service.validation;

import static gov.ca.cwds.idm.service.validation.ValidationServiceImpl.PHONE_EXTENSION_PATTERN;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class PhoneExtensionFormatValidatorTest {

  private OptionalPropertyPatternValidator validator;

  @Before
  public void before() {
    validator = new OptionalPropertyPatternValidator(PHONE_EXTENSION_PATTERN);
  }

  @Test
  public void testIsValid() {
    assertFalse(validator.isValid("+012"));
    assertFalse(validator.isValid("1 5"));
    assertFalse(validator.isValid("2-3"));
    assertFalse(validator.isValid("(012)"));
    assertFalse(validator.isValid("012345678"));

    assertTrue(validator.isValid(null));
    assertTrue(validator.isValid(""));
    assertTrue(validator.isValid("  "));
    assertTrue(validator.isValid("1234567"));
    assertTrue(validator.isValid("456"));
    assertTrue(validator.isValid("1"));
    assertTrue(validator.isValid("0"));
  }
}
