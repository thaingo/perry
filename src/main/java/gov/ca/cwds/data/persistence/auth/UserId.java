package gov.ca.cwds.data.persistence.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.CmsPersistentObject;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

/**
 * {@link PersistentObject} representing a UserId
 *
 * @author CWDS API Team
 */

@Entity
@Table(name = "USERID_T")
public class UserId extends CmsPersistentObject {

  /**
   * Base serialization version. Increment per version of this class.
   */
  private static final long serialVersionUID = 1L;

  @Type(type = "date")
  @Column(name = "END_DT")
  private Date endDate;

  @JsonFormat(pattern = "HH:mm:ss")
  @Type(type = "time")
  @Column(name = "END_TM")
  private Date endTime;

  @Column(name = "FKFPSTFPRT", length = CMS_ID_LEN)
  private String fkfpstfprt;

  @Column(name = "FKSTFPERST", length = CMS_ID_LEN)
  private String staffPersonId;

  @Id
  @Column(name = "IDENTIFIER", length = CMS_ID_LEN)
  private String id;

  @Column(name = "LOGON_ID")
  private String logonId;

  @Type(type = "short")
  @Column(name = "SYS_DMC")
  private Short systemDomainType;

  @OneToMany
  @JoinColumn(name = "FKUSERID_T")
  @LazyCollection(LazyCollectionOption.FALSE)
  @Fetch(FetchMode.JOIN)
  private Set<StaffAuthorityPrivilege> privileges;

  @ManyToOne
  @JoinColumn(name = "FKSTFPERST", insertable = false, updatable = false)
  @LazyCollection(LazyCollectionOption.FALSE)
  @Fetch(FetchMode.JOIN)
  private StaffPerson staffPerson;

  /**
   * Default constructor <p> Required for Hibernate
   */
  public UserId() {
    super();
  }

  /**
   * Constructor
   *
   * @param endDate The endDate
   * @param endTime The endTime
   * @param fkfpstfprt The fkfpstfprt
   * @param staffPersonId The staffPersonId
   * @param id The id
   * @param logonId The logonId
   * @param systemDomainType The system domain type
   */
  public UserId(Date endDate, Date endTime, String fkfpstfprt, String staffPersonId, String id,
      String logonId, Short systemDomainType) {
    super();
    this.endDate = endDate;
    this.endTime = endTime;
    this.fkfpstfprt = fkfpstfprt;
    this.staffPersonId = staffPersonId;
    this.id = id;
    this.logonId = logonId;
    this.systemDomainType = systemDomainType;
  }

  public StaffPerson getStaffPerson() {
    return staffPerson;
  }

  public void setStaffPerson(StaffPerson staffPerson) {
    this.staffPerson = staffPerson;
  }

  public Set<StaffAuthorityPrivilege> getPrivileges() {
    return privileges;
  }

  public void setPrivileges(
      Set<StaffAuthorityPrivilege> privileges) {
    this.privileges = privileges;
  }

  /**
   * {@inheritDoc}
   *
   * @see PersistentObject#getPrimaryKey()
   */
  @Override
  public String getPrimaryKey() {
    return getId();
  }

  /**
   * @return the endDate
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * @return the endTime
   */
  public Date getEndTime() {
    return endTime;
  }

  /**
   * @return the fkfpstfprt
   */
  public String getFkfpstfprt() {
    return StringUtils.trimToEmpty(fkfpstfprt);
  }

  /**
   * @return the staffPersonId
   */
  public String getStaffPersonId() {
    return StringUtils.trimToEmpty(staffPersonId);
  }

  /**
   * @return the id
   */
  public String getId() {
    return StringUtils.trimToEmpty(id);
  }

  /**
   * @return the logonId
   */
  public String getLogonId() {
    return StringUtils.trimToEmpty(logonId);
  }

  /**
   * @return the systemDomainType
   */
  public Short getSystemDomainType() {
    return systemDomainType;
  }


  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserId)) {
      return false;
    }
    UserId userId = (UserId) o;
    return Objects.equals(getEndDate(), userId.getEndDate()) &&
        Objects.equals(getEndTime(), userId.getEndTime()) &&
        Objects.equals(getFkfpstfprt(), userId.getFkfpstfprt()) &&
        Objects.equals(getStaffPersonId(), userId.getStaffPersonId()) &&
        Objects.equals(getId(), userId.getId()) &&
        Objects.equals(getLogonId(), userId.getLogonId()) &&
        Objects.equals(getSystemDomainType(), userId.getSystemDomainType()) &&
        Objects.equals(getPrivileges(), userId.getPrivileges()) &&
        Objects.equals(getStaffPerson(), userId.getStaffPerson()) &&
        Objects.equals(getLastUpdatedTime(), userId.getLastUpdatedTime()) &&
        Objects.equals(getLastUpdatedId(), userId.getLastUpdatedId());
  }

  @Override
  public final int hashCode() {

    return Objects
        .hash(getEndDate(), getEndTime(), getFkfpstfprt(), getStaffPersonId(), getId(),
            getLogonId(),
            getSystemDomainType(), getPrivileges(), getStaffPerson(),
            getLastUpdatedTime(), getLastUpdatedId());
  }
}
