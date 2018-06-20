package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserVerificationResult implements Serializable {


  private static final long serialVersionUID = 6613962034554161045L;

  private User user;
  private boolean verificationPassed;
  private String verificationMessage;

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


  public static final class Builder {
    private User user;
    private boolean verificationPassed;
    private String verificationMessage;

    private Builder() {
    }

    public static Builder anUserVerificationResult() {
      return new Builder();
    }

    public Builder withUser(User user) {
      this.user = user;
      return this;
    }

    public Builder withVerificationPassed(boolean verificationPassed) {
      this.verificationPassed = verificationPassed;
      return this;
    }

    public Builder withMessage(String verificationMessage) {
      this.verificationMessage = verificationMessage;
      return this;
    }

    public UserVerificationResult build() {
      UserVerificationResult userVerificationResult = new UserVerificationResult();
      userVerificationResult.setUser(user);
      userVerificationResult.setVerificationPassed(verificationPassed);
      userVerificationResult.setVerificationMessage(verificationMessage);
      return userVerificationResult;
    }
  }
}
