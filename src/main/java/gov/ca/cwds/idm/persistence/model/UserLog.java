package gov.ca.cwds.idm.persistence.model;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_log")
public class UserLog {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @NotNull
  @Size(min = 1, max = 128)
  @Column(name = "username")
  private String username;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "operation")
  private Operation operation;

  @NotNull
  @Column(name = "operation_time")
  private LocalDateTime operationTime;

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Operation getOperation() {
    return operation;
  }

  public void setOperation(Operation operation) {
    this.operation = operation;
  }

  public LocalDateTime getOperationTime() {
    return operationTime;
  }

  public void setOperationTime(LocalDateTime operationTime) {
    this.operationTime = operationTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserLog)) {
      return false;
    }
    UserLog userLog = (UserLog) o;
    return Objects.equals(getUsername(), userLog.getUsername()) &&
        getOperation() == userLog.getOperation() &&
        Objects.equals(getOperationTime(), userLog.getOperationTime());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUsername(), getOperation(), getOperationTime());
  }
}
