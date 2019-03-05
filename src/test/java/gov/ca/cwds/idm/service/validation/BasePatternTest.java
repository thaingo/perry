package gov.ca.cwds.idm.service.validation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public abstract class BasePatternTest {

  private final PatternValidator patternValidator;

  public BasePatternTest(PatternValidator patternValidator) {
    this.patternValidator = patternValidator;
  }

  protected final void valid(String str) {
    assertTrue(patternValidator.isValid(str));
  }

  protected final void invalid(String str) {
    assertFalse(patternValidator.isValid(str));
  }
}
