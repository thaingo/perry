package gov.ca.cwds.idm.persistence.model;

import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "permission")
public class Permission {

  @Id
  @NotNull
  @Column(name = "name")
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Permission)) {
      return false;
    }
    Permission permission = (Permission) o;
    return Objects.equals(getName(), permission.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }
}
