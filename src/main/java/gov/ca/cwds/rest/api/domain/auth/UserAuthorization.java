package gov.ca.cwds.rest.api.domain.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Set;

/**
 * @author CWDS API Team
 */
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public final class UserAuthorization {

  /**
   * default
   */
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(example = "ABC")
  @JsonProperty("user_id")
  private String userId;

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

  @ApiModelProperty(required = false, readOnly = false)
  @JsonProperty("cws_office")
  private CwsOffice cwsOffice;

  @ApiModelProperty
  @JsonProperty("staff_person")
  private StaffPerson staffPerson;

  @ApiModelProperty
  @JsonProperty("has_assignment")
  private Boolean hasAssignment;

  /**
   * Default, no-param, no-op constructor Required by frameworks.
   */
  public UserAuthorization() {
    // required by framework
  }

  /**
   * JSON Constructor
   *
   * @param userId the user id
   * @param socialWorker is user a social worker
   * @param supervisor is user a supervisor
   * @param overrideAuthority does user have override authority
   * @param authPrivilege the authorityPrivilege
   * @param unitAuthority the unitAuthority
   * @param cwsOffice the cwsOffice
   */
  public UserAuthorization(@JsonProperty("user_id") String userId,
      @JsonProperty("social_worker") Boolean socialWorker,
      @JsonProperty("supervisor") Boolean supervisor,
      @JsonProperty("override_authority") Boolean overrideAuthority,
      @JsonProperty("authority_privilege") Set<StaffAuthorityPrivilege> authPrivilege,
      @JsonProperty("unit_authority") Set<StaffUnitAuthority> unitAuthority,
      @JsonProperty("cws_office") CwsOffice cwsOffice,
      @JsonProperty("staff_person") StaffPerson staffPerson,
      @JsonProperty("has_assignment") Boolean hasAssignment) {
    super();
    this.userId = userId;
    this.socialWorker = socialWorker;
    this.supervisor = supervisor;
    this.overrideAuthority = overrideAuthority;
    this.authorityPrivilege = authPrivilege;
    this.unitAuthority = unitAuthority;
    this.cwsOffice = cwsOffice;
    this.staffPerson = staffPerson;
    this.hasAssignment = hasAssignment;
  }

  /**
   * @return the userId
   */
  public String getUserId() {
    return userId;
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

  /**
   * @return the cwsOffice
   */
  public CwsOffice getCwsOffice() {
    return cwsOffice;
  }

  public StaffPerson getStaffPerson() {
    return staffPerson;
  }

  /**
   * @return true if user has assignment
   */
  public Boolean getHasAssignment() {
    return hasAssignment;
  }

  public void setHasAssignment(Boolean hasAssignment) {
    this.hasAssignment = hasAssignment;
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
    result = prime * result + ((cwsOffice == null) ? 0 : cwsOffice.hashCode());
    result = prime * result + ((overrideAuthority == null) ? 0 : overrideAuthority.hashCode());
    result = prime * result + ((socialWorker == null) ? 0 : socialWorker.hashCode());
    result = prime * result + ((supervisor == null) ? 0 : supervisor.hashCode());
    result = prime * result + ((unitAuthority == null) ? 0 : unitAuthority.hashCode());
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
    result = prime * result + ((staffPerson == null) ? 0 : staffPerson.hashCode());
    result = prime * result + ((hasAssignment == null) ? 0 : hasAssignment.hashCode());
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
    if (cwsOffice == null) {
      if (other.cwsOffice != null) {
        return false;
      }
    } else if (!cwsOffice.equals(other.cwsOffice)) {
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
    if (staffPerson == null) {
      if (other.staffPerson != null) {
        return false;
      }
    } else if (!staffPerson.equals(other.staffPerson)) {
      return false;
    }
    if (hasAssignment == null) {
      if (other.hasAssignment != null) {
        return false;
      }
    } else if (!hasAssignment.equals(other.hasAssignment)) {
      return false;
    }
    return true;
  }


  public static final class UserAuthorizationBuilder {

    private String userId;
    private Boolean socialWorker;
    private Boolean supervisor;
    private Boolean overrideAuthority;
    private Set<StaffAuthorityPrivilege> authorityPrivilege;
    private Set<StaffUnitAuthority> unitAuthority;
    private CwsOffice cwsOffice;
    private StaffPerson staffPerson;
    private Boolean hasAssignment;

    private UserAuthorizationBuilder() {
    }

    public static UserAuthorizationBuilder anUserAuthorization() {
      return new UserAuthorizationBuilder();
    }

    public UserAuthorizationBuilder withUserId(String userId) {
      this.userId = userId;
      return this;
    }

    public UserAuthorizationBuilder withSocialWorker(Boolean socialWorker) {
      this.socialWorker = socialWorker;
      return this;
    }

    public UserAuthorizationBuilder withSupervisor(Boolean supervisor) {
      this.supervisor = supervisor;
      return this;
    }

    public UserAuthorizationBuilder withOverrideAuthority(Boolean overrideAuthority) {
      this.overrideAuthority = overrideAuthority;
      return this;
    }

    public UserAuthorizationBuilder withAuthorityPrivilege(
        Set<StaffAuthorityPrivilege> authorityPrivilege) {
      this.authorityPrivilege = authorityPrivilege;
      return this;
    }

    public UserAuthorizationBuilder withUnitAuthority(Set<StaffUnitAuthority> unitAuthority) {
      this.unitAuthority = unitAuthority;
      return this;
    }

    public UserAuthorizationBuilder withCwsOffice(CwsOffice cwsOffice) {
      this.cwsOffice = cwsOffice;
      return this;
    }

    public UserAuthorizationBuilder withStaffPerson(StaffPerson staffPerson) {
      this.staffPerson = staffPerson;
      return this;
    }

    public UserAuthorizationBuilder withHasAssignment(Boolean hasAssignment) {
      this.hasAssignment = hasAssignment;
      return this;
    }

    public UserAuthorization build() {
      UserAuthorization userAuthorization = new UserAuthorization(userId, socialWorker, supervisor,
          overrideAuthority, null, unitAuthority, cwsOffice, staffPerson, hasAssignment);
      userAuthorization.authorityPrivilege = this.authorityPrivilege;
      return userAuthorization;
    }
  }
}
