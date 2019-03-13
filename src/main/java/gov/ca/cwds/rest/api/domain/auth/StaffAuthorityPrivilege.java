package gov.ca.cwds.rest.api.domain.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.data.persistence.auth.CmsUserAuthPrivilege;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

}
