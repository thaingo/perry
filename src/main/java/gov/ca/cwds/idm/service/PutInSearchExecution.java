package gov.ca.cwds.idm.service;

import com.amazonaws.services.cognitoidp.model.UserType;

public abstract class PutInSearchExecution extends TryCatchExecution<UserType> {

  private TryCatchExecution<String> userLogExecution;

  public PutInSearchExecution(
      UserType input) {
    super(input);
  }

  public TryCatchExecution<String> getUserLogExecution() {
    return userLogExecution;
  }

  public void setUserLogExecution(
      TryCatchExecution<String> userLogExecution) {
    this.userLogExecution = userLogExecution;
  }
}
