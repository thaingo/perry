package gov.ca.cwds.idm.persistence.model;

import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Cacheable
@Table(name = "role")
public class Role {

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
    if (!(o instanceof Role)) {
      return false;
    }
    Role role = (Role) o;
    return Objects.equals(getName(), role.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }
}
