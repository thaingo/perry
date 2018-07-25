package gov.ca.cwds.idm.persistence.model;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_log")
public class UserLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Size(min = 1, max = 128)
  @Column(name = "username")
  private String username;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "operation_type")
  private OperationType operationType;

  @NotNull
  @Column(name = "operation_time")
  private Date operationTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public OperationType getOperationType() {
    return operationType;
  }

  public void setOperationType(OperationType operationType) {
    this.operationType = operationType;
  }

  public Date getOperationTime() {
    return operationTime;
  }

  public void setOperationTime(Date operationTime) {
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
        getOperationType() == userLog.getOperationType() &&
        Objects.equals(getOperationTime(), userLog.getOperationTime());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUsername(), getOperationType(), getOperationTime());
  }
}
