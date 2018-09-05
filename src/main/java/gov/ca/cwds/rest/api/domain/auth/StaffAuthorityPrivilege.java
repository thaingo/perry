package gov.ca.cwds.rest.api.domain.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.data.persistence.auth.CmsUserAuthPrivilege;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Class representing a Staff Person Authority Privilege.
 *
 * @author CWDS API Team
 */
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StaffAuthorityPrivilege {

  @ApiModelProperty(example = "1482")
  @JsonProperty("auth_privilege_type")
  private String authPrivilegeType;

  @ApiModelProperty(example = "Sensitive Persons")
  @JsonProperty("auth_privilege_type_desc")
  private String authPrivilegeTypeDesc;

  @ApiModelProperty(example = "P")
  @JsonProperty("auth_privilege_code")
  private String authPrivilegeCode;

  @ApiModelProperty(example = "Staff Person Privilege Type")
  @JsonProperty("auth_privilege_code_desc")
  private String authPrivilegeCodeDesc;

  @ApiModelProperty(example = "21")
  @JsonProperty("county_code")
  private String countyCode;

  @ApiModelProperty(example = "Marin")
  @JsonProperty("county")
  private String county;

  @ApiModelProperty(example = "2012-04-01")
  @JsonProperty("end_date")
  private String endDate;

  /**
   * JSON Constructor
   *
   * @param authPrivilegeType the authority privilege type
   * @param authPrivilegeCode the authority privilege code
   * @param countyCode the county code
   * @param endDate the endDate
   */
  public StaffAuthorityPrivilege(
      @JsonProperty("auth_privilege_type") String authPrivilegeType,
      @JsonProperty("auth_privilege_code") String authPrivilegeCode,
      @JsonProperty("county_code") String countyCode,
      @JsonProperty("end_date") String endDate) {
    super();
    this.authPrivilegeType = authPrivilegeType;
    this.authPrivilegeTypeDesc =
        CmsUserAuthPrivilege.getInstance().getUserAuthPrivDescription(authPrivilegeType);
    this.authPrivilegeCode = authPrivilegeCode;
    this.authPrivilegeCodeDesc = AuthPrivilege.getAuthPrivilegeDescription(authPrivilegeCode);
    this.countyCode = countyCode;
    this.endDate = endDate;
    this.county = GovernmentEntityType.findByCountyCd(countyCode).getDescription();
  }

  /** @return the authPrivilegeType */
  public String getAuthPrivilegeType() {
    return authPrivilegeType;
  }

  /** @return the endDate */
  public String getEndDate() {
    return endDate;
  }

  /** @return the authPrivilegeTypeDesc */
  public String getAuthPrivilegeTypeDesc() {
    return authPrivilegeTypeDesc;
  }

  /** @return the authPrivilegeCode */
  public String getAuthPrivilegeCode() {
    return authPrivilegeCode;
  }

  /** @return the authPrivilegeCodeDesc */
  public String getAuthPrivilegeCodeDesc() {
    return authPrivilegeCodeDesc;
  }

  /** @return the countyCode */
  public String getCountyCode() {
    return countyCode;
  }

  /** @return the county */
  public String getCounty() {
    return county;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((authPrivilegeCode == null) ? 0 : authPrivilegeCode.hashCode());
    result =
        prime * result + ((authPrivilegeCodeDesc == null) ? 0 : authPrivilegeCodeDesc.hashCode());
    result = prime * result + ((authPrivilegeType == null) ? 0 : authPrivilegeType.hashCode());
    result =
        prime * result + ((authPrivilegeTypeDesc == null) ? 0 : authPrivilegeTypeDesc.hashCode());
    result = prime * result + ((county == null) ? 0 : county.hashCode());
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
    result = prime * result + ((countyCode == null) ? 0 : countyCode.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    StaffAuthorityPrivilege other = (StaffAuthorityPrivilege) obj;
    if (authPrivilegeCode == null) {
      if (other.authPrivilegeCode != null) {
        return false;
      }
    } else if (!authPrivilegeCode.equals(other.authPrivilegeCode)) {
      return false;
    }
    if (authPrivilegeCodeDesc == null) {
      if (other.authPrivilegeCodeDesc != null) {
        return false;
      }
    } else if (!authPrivilegeCodeDesc.equals(other.authPrivilegeCodeDesc)) {
      return false;
    }
    if (authPrivilegeType == null) {
      if (other.authPrivilegeType != null) {
        return false;
      }
    } else if (!authPrivilegeType.equals(other.authPrivilegeType)) {
      return false;
    }
    if (authPrivilegeTypeDesc == null) {
      if (other.authPrivilegeTypeDesc != null) {
        return false;
      }
    } else if (!authPrivilegeTypeDesc.equals(other.authPrivilegeTypeDesc)) {
      return false;
    }
    if (county == null) {
      if (other.county != null) {
        return false;
      }
    } else if (!county.equals(other.county)) {
      return false;
    }
    if (endDate == null) {
      if (other.endDate != null) {
        return false;
      }
    } else if (!endDate.equals(other.endDate)) {
      return false;
    }
    if (countyCode == null) {
      if (other.countyCode != null) {
        return false;
      }
    } else if (!countyCode.equals(other.countyCode)) {
      return false;
    }
    return true;
  }
}
