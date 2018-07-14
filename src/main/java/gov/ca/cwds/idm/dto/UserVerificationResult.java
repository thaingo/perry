package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserVerificationResult implements Serializable {

  private static final long serialVersionUID = 4868787126860023981L;

  private User user;
  private boolean verificationPassed;
  private String verificationMessage;
  private String errorCode;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isVerificationPassed() {
    return verificationPassed;
  }

  public void setVerificationPassed(boolean verificationPassed) {
    this.verificationPassed = verificationPassed;
  }

  public String getVerificationMessage() {
    return verificationMessage;
  }

  public void setVerificationMessage(String verificationMessage) {
    this.verificationMessage = verificationMessage;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }


  public static final class Builder {
    private User user;
    private boolean verificationPassed;
    private String verificationMessage;
    private String errorCode;

    private Builder() {
    }

    public static Builder anUserVerificationResult() {
      return new Builder();
    }

    public Builder withUser(User user) {
      this.user = user;
      return this;
    }

    public Builder withVerificationPassed() {
      this.verificationPassed = true;
      return this;
    }

    public Builder withMessage(String verificationMessage) {
      this.verificationMessage = verificationMessage;
      return this;
    }

    public Builder withVerificationFailed(String errorCode, String verificationMessage) {
      this.verificationPassed = false;
      this.verificationMessage = verificationMessage;
      this.errorCode = errorCode;
      return this;
    }

    public UserVerificationResult build() {
      UserVerificationResult userVerificationResult = new UserVerificationResult();
      userVerificationResult.setUser(user);
      userVerificationResult.setVerificationPassed(verificationPassed);
      userVerificationResult.setVerificationMessage(verificationMessage);
      userVerificationResult.setErrorCode(errorCode);
      return userVerificationResult;
    }
  }
}
