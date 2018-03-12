package gov.ca.cwds.rest.api.domain.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.rest.api.Request;
import gov.ca.cwds.rest.api.Response;
import gov.ca.cwds.rest.api.domain.DomainObject;
import io.dropwizard.jackson.JsonSnakeCase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

/**
 * {@link DomainObject} representing a request for User Authorization.
 *
 * @author CWDS API Team
 */
@ApiModel
@JsonSnakeCase
public final class UserAuthorization extends DomainObject implements Request, Response {

  /**
   * default
   */
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(example = "ABC")
  @JsonProperty("user_id")
  private String userId;

  @ApiModelProperty(example = "DEF")
  @JsonProperty("staff_person_id")
  private String staffPersonId;

  @ApiModelProperty(required = true, readOnly = false)
  @JsonProperty("social_worker")
  private Boolean socialWorker;

  @ApiModelProperty(required = true, readOnly = false)
  @JsonProperty("supervisor")
  private Boolean supervisor;

  @ApiModelProperty(required = true, readOnly = false)
  @JsonProperty("override_authority")
  private Boolean overrideAuthority;

  @ApiModelProperty(required = false, readOnly = false)
  @JsonProperty("authority_privilege")
  private Set<StaffAuthorityPrivilege> authorityPrivilege;

  @ApiModelProperty(required = false, readOnly = false)
  @JsonProperty("unit_authority")
  private Set<StaffUnitAuthority> unitAuthority;

  @ApiModelProperty
  @JsonProperty("countyName")
  private String countyName;

  @ApiModelProperty
  @JsonProperty("countyCode")
  private String countyCode;

  @ApiModelProperty
  @JsonProperty("countyCwsCode")
  private String countyCwsCode;

  /**
   * Default, no-param, no-op constructor Required by frameworks.
   */
  public UserAuthorization() {
    // required by framework
  }

  /**
   * JSON Constructor
   *  @param userId            the user id
   * @param staffPersonId     the staff person id
   * @param socialWorker      is user a social worker
   * @param supervisor        is user a supervisor
   * @param overrideAuthority does user have override authority
   * @param authPrivilege     the authorityPrivilege
   * @param unitAuthority     the unitAuthority
   */
  public UserAuthorization(@JsonProperty("user_id") String userId,
                           @JsonProperty("staff_person_id") String staffPersonId,
                           @JsonProperty("social_worker") Boolean socialWorker,
                           @JsonProperty("supervisor") Boolean supervisor,
                           @JsonProperty("override_authority") Boolean overrideAuthority,
                           @JsonProperty("authority_privilege") Set<StaffAuthorityPrivilege> authPrivilege,
                           @JsonProperty("unit_authority") Set<StaffUnitAuthority> unitAuthority,
                           @JsonProperty("county_name") String countyName,
                           @JsonProperty("county_code") String countyCode,
                           @JsonProperty("county_cws_code") String countyCwsCode) {
    super();
    this.userId = userId;
    this.staffPersonId = staffPersonId;
    this.socialWorker = socialWorker;
    this.supervisor = supervisor;
    this.overrideAuthority = overrideAuthority;
    this.authorityPrivilege = authPrivilege;
    this.unitAuthority = unitAuthority;
    this.countyName = countyName;
    this.countyCode = countyCode;
    this.countyCwsCode = countyCwsCode;
  }

  /**
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @return the staffPersonId
   */
  public String getStaffPersonId() {
    return staffPersonId;
  }

  /**
   * @return the socialWorker
   */
  public Boolean getSocialWorker() {
    return socialWorker;
  }

  /**
   * @return the supervisor
   */
  public Boolean getSupervisor() {
    return supervisor;
  }

  /**
   * @return the overrideAuthority
   */
  public Boolean getOverrideAuthority() {
    return overrideAuthority;
  }

  /**
   * @return the authorityPrivilege
   */
  public Set<StaffAuthorityPrivilege> getAuthorityPrivilege() {
    return authorityPrivilege;
  }

  /**
   * @return the unitAuthority
   */
  public Set<StaffUnitAuthority> getUnitAuthority() {
    return unitAuthority;
  }

  public String getCountyName() {
    return countyName;
  }

  public String getCountyCode() {
    return countyCode;
  }

  public String getCountyCwsCode() {
    return countyCwsCode;
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
    result = prime * result + ((authorityPrivilege == null) ? 0 : authorityPrivilege.hashCode());
    result = prime * result + ((overrideAuthority == null) ? 0 : overrideAuthority.hashCode());
    result = prime * result + ((socialWorker == null) ? 0 : socialWorker.hashCode());
    result = prime * result + ((staffPersonId == null) ? 0 : staffPersonId.hashCode());
    result = prime * result + ((supervisor == null) ? 0 : supervisor.hashCode());
    result = prime * result + ((unitAuthority == null) ? 0 : unitAuthority.hashCode());
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
    result = prime * result + ((countyName == null) ? 0 : countyName.hashCode());
    result = prime * result + ((countyCode == null) ? 0 : countyCode.hashCode());
    result = prime * result + ((countyCwsCode == null) ? 0 : countyCwsCode.hashCode());
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
    UserAuthorization other = (UserAuthorization) obj;
    if (authorityPrivilege == null) {
      if (other.authorityPrivilege != null) {
        return false;
      }
    } else if (!authorityPrivilege.equals(other.authorityPrivilege)) {
      return false;
    }
    if (overrideAuthority == null) {
      if (other.overrideAuthority != null) {
        return false;
      }
    } else if (!overrideAuthority.equals(other.overrideAuthority)) {
      return false;
    }
    if (socialWorker == null) {
      if (other.socialWorker != null) {
        return false;
      }
    } else if (!socialWorker.equals(other.socialWorker)) {
      return false;
    }
    if (staffPersonId == null) {
      if (other.staffPersonId != null) {
        return false;
      }
    } else if (!staffPersonId.equals(other.staffPersonId)) {
      return false;
    }
    if (supervisor == null) {
      if (other.supervisor != null) {
        return false;
      }
    } else if (!supervisor.equals(other.supervisor)) {
      return false;
    }
    if (unitAuthority == null) {
      if (other.unitAuthority != null) {
        return false;
      }
    } else if (!unitAuthority.equals(other.unitAuthority)) {
      return false;
    }
    if (userId == null) {
      if (other.userId != null) {
        return false;
      }
    } else if (!userId.equals(other.userId)) {
      return false;
    }
    if (countyName == null) {
      if (other.countyName != null) {
        return false;
      }
    } else if (!countyName.equals(other.countyName)) {
      return false;
    }
    if (countyCode == null) {
      if (other.countyCode != null) {
        return false;
      }
    } else if (!countyCode.equals(other.countyCode)) {
      return false;
    }
    if (countyCwsCode == null) {
      if (other.countyCwsCode != null) {
        return false;
      }
    } else if (!countyCwsCode.equals(other.countyCwsCode)) {
      return false;
    }
    return true;
  }


}
