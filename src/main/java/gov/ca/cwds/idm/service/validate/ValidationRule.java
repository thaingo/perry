package gov.ca.cwds.idm.service.validate;

/** Validates all rules imposed by the class. */
public interface ValidationRule {

  /** Performs the validation here. */
  void performValidation(String str);
}
