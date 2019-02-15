package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

/** DTO class representing a CWS Staff Person Authority Privilege. */
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CwsStaffPrivilege implements Serializable {

  private static final long serialVersionUID = 9L;

  @ApiModelProperty(example = "Access Authority")
  private String category;

  @ApiModelProperty(example = "Adoptions")
  private String privilege;

  public CwsStaffPrivilege(
      @JsonProperty("category") String category, @JsonProperty("privilege") String privilege) {
    this.category = category;
    this.privilege = privilege;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getPrivilege() {
    return privilege;
  }

  public void setPrivilege(String privilege) {
    this.privilege = privilege;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CwsStaffPrivilege)) {
      return false;
    }
    CwsStaffPrivilege that = (CwsStaffPrivilege) o;
    return category.equals(that.category) && privilege.equals(that.privilege);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, privilege);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("category", category)
        .append("privilege", privilege)
        .toString();
  }
}
