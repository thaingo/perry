package gov.ca.cwds.data.persistence.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.CmsPersistentObject;
import java.util.Date;
import java.util.List;
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

  /**
   * {@inheritDoc}
   *
   * @see Object#hashCode()
   */
  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
    result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
    result = prime * result + ((fkfpstfprt == null) ? 0 : fkfpstfprt.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((logonId == null) ? 0 : logonId.hashCode());
    result = prime * result + ((staffPersonId == null) ? 0 : staffPersonId.hashCode());
    result = prime * result + ((systemDomainType == null) ? 0 : systemDomainType.hashCode());
    result = prime * result
        + ((super.getLastUpdatedId() == null) ? 0 : super.getLastUpdatedId().hashCode());
    result = prime * result
        + ((super.getLastUpdatedTime() == null) ? 0 : super.getLastUpdatedTime().hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#equals(Object)
   */
  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof UserId)) {
      return false;
    }
    UserId other = (UserId) obj;
    if (endDate == null) {
      if (other.endDate != null) {
        return false;
      }
    } else if (!endDate.equals(other.endDate)) {
      return false;
    }
    if (endTime == null) {
      if (other.endTime != null) {
        return false;
      }
    } else if (!endTime.equals(other.endTime)) {
      return false;
    }
    if (fkfpstfprt == null) {
      if (other.fkfpstfprt != null) {
        return false;
      }
    } else if (!fkfpstfprt.equals(other.fkfpstfprt)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (logonId == null) {
      if (other.logonId != null) {
        return false;
      }
    } else if (!logonId.equals(other.logonId)) {
      return false;
    }
    if (staffPersonId == null) {
      if (other.staffPersonId != null) {
        return false;
      }
    } else if (!staffPersonId.equals(other.staffPersonId)) {
      return false;
    }
    if (systemDomainType == null) {
      if (other.systemDomainType != null) {
        return false;
      }
    } else if (!systemDomainType.equals(other.systemDomainType)) {
      return false;
    }
    if (super.getLastUpdatedId() == null) {
      if (other.getLastUpdatedId() != null) {
        return false;
      }
    } else if (!super.getLastUpdatedId().equals(other.getLastUpdatedId())) {
      return false;
    }
    if (super.getLastUpdatedTime() == null) {
      if (other.getLastUpdatedTime() != null) {
        return false;
      }
    } else if (!super.getLastUpdatedTime().equals(other.getLastUpdatedTime())) {
      return false;
    }
    return true;
  }

}
