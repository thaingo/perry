package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.idm.persistence.model.OperationType;
import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserAndOperation implements Serializable {

  private static final long serialVersionUID = 2272859030318351815L;

  private final User user;

  private final OperationType operation;

  public UserAndOperation(User user, OperationType operation) {
    this.user = user;
    this.operation = operation;
  }

  public User getUser() {
    return user;
  }

  public OperationType getOperation() {
    return operation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserAndOperation)) {
      return false;
    }
    UserAndOperation that = (UserAndOperation) o;
    return Objects.equals(user.getId(), that.user.getId()) &&
        operation == that.operation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(user.getId(), operation);
  }
}
