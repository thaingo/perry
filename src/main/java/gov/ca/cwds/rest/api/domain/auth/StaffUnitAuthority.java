package gov.ca.cwds.rest.api.domain.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Class representing a Staff Person Unit of Authority.
 *
 * @author CWDS API Team
 */
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StaffUnitAuthority {

  @ApiModelProperty(example = "R")
  @JsonProperty("unit_authority_code")
  private String unitAuthorityCode;

  @ApiModelProperty(example = "Unitwide Read")
  @JsonProperty("unit_authority_code_desc")
  private String unitAuthorityCodeDesc;

  @ApiModelProperty(example = "O2ABcDe00F")
  @JsonProperty("assigned_unit")
  private String assignedUnit;

  @ApiModelProperty(example = "2012-04-01")
  @JsonProperty("assigned_unit_end_date")
  private String assignedUnitEndDate;

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
   * @param unitAuthorityType   the unit authority
   * @param assignedUnit        the assigned unit
   * @param assignedUnitEndDate the end date for the assigned unit
   * @param countyCode          the county code
   * @param endDate             the endDate
   */
  public StaffUnitAuthority(@JsonProperty("unit_authority_code") String unitAuthorityType,
                            @JsonProperty("assigned_unit") String assignedUnit,
                            @JsonProperty("assigned_unit_end_date") String assignedUnitEndDate,
                            @JsonProperty("county_code") String countyCode, @JsonProperty("end_date") String endDate) {
    super();
    this.unitAuthorityCode = unitAuthorityType;
    this.unitAuthorityCodeDesc =
            UnitAuthority.findByUnitAuthorityCode(unitAuthorityType).getDescription();
    this.assignedUnit = assignedUnit;
    this.assignedUnitEndDate = assignedUnitEndDate;
    this.countyCode = countyCode;
    this.county = GovernmentEntityType.findByCountyCd(countyCode).getDescription();
    this.endDate = endDate;
  }


  /**
   * @return the unitAuthorityType
   */
  public String getUnitAuthorityCode() {
    return unitAuthorityCode;
  }


  /**
   * @return the unitAuthorityTypeDesc
   */
  public String getUnitAuthorityCodeDesc() {
    return unitAuthorityCodeDesc;
  }


  /**
   * @return the assignedUnit
   */
  public String getAssignedUnit() {
    return assignedUnit;
  }


  /**
   * @return the assignedUnitEndDate
   */
  public String getAssignedUnitEndDate() {
    return assignedUnitEndDate;
  }


  /**
   * @return the countyCode
   */
  public String getCountyCode() {
    return countyCode;
  }


  /**
   * @return the county
   */
  public String getCounty() {
    return county;
  }


  /**
   * @return the endDate
   */
  public String getEndDate() {
    return endDate;
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
