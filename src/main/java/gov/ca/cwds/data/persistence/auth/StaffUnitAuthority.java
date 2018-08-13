package gov.ca.cwds.data.persistence.auth;

import gov.ca.cwds.data.persistence.cms.CmsPersistentObject;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

/**
 * {@link CmsPersistentObject} representing a StaffPersonUnitAuthority.
 *
 * @author CWDS API Team
 */
@Entity
@Table(name = "STFUATHT")
public class StaffUnitAuthority extends CmsPersistentObject {

  /**
   * Base serialization version. Increment per version of this class.
   */
  private static final long serialVersionUID = 1L;

  @Column(name = "UNTAUTH_CD")
  private String authorityCode;

  @Column(name = "CNTY_SPFCD")
  private String countySpecificCode;

  @Type(type = "date")
  @Column(name = "END_DT")
  private Date endDate;

  @Column(name = "FKASG_UNIT")
  private String fkasgUnit;

  @Column(name = "FKSTFPERST")
  private String staffPersonId;

  @Type(type = "date")
  @Column(name = "START_DT")
  private Date startDate;

  @Id
  @Column(name = "THIRD_ID")
  private String thirdId;

  @ManyToOne
  @JoinColumn(name = "FKASG_UNIT", insertable = false, updatable = false)
  private AssignmentUnit assignmentUnit;

  /**
   * Default constructor <p> Required for Hibernate
   */
  public StaffUnitAuthority() {
    super();
  }

  /**
   * @param authorityCode The authorityCode
   * @param countySpecificCode The countySpecificCode
   * @param endDate The endDate
   * @param fkasgUnit The fkasgUnit
   * @param staffPersonId The staffPersonId
   * @param startDate The startDate
   * @param thirdId The thirdId
   */
  public StaffUnitAuthority(String authorityCode, String countySpecificCode, Date endDate,
      String fkasgUnit, String staffPersonId, Date startDate, String thirdId) {
    super();
    this.authorityCode = authorityCode;
    this.countySpecificCode = countySpecificCode;
    this.endDate = endDate;
    this.fkasgUnit = fkasgUnit;
    this.staffPersonId = staffPersonId;
    this.startDate = startDate;
    this.thirdId = thirdId;
  }

  public AssignmentUnit getAssignmentUnit() {
    return assignmentUnit;
  }

  public void setAssignmentUnit(AssignmentUnit assignmentUnit) {
    this.assignmentUnit = assignmentUnit;
  }

  /**
   * {@inheritDoc}
   *
   * @see gov.ca.cwds.data.persistence.PersistentObject#getPrimaryKey()
   */
  @Override
  public String getPrimaryKey() {
    return getThirdId();
  }

  /**
   * @return the authorityCode
   */
  public String getAuthorityCode() {
    return StringUtils.trimToEmpty(authorityCode);
  }

  /**
   * @return the countySpecificCode
   */
  public String getCountySpecificCode() {
    return StringUtils.trimToEmpty(countySpecificCode);
  }

  /**
   * @return the endDate
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * @return the fkasgUnit
   */
  public String getFkasgUnit() {
    return StringUtils.trimToEmpty(fkasgUnit);
  }

  /**
   * @return the staffPersonId
   */
  public String getStaffPersonId() {
    return StringUtils.trimToEmpty(staffPersonId);
  }

  /**
   * @return the startDate
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * @return the thirdId
   */
  public String getThirdId() {
    return StringUtils.trimToEmpty(thirdId);
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
    result = prime * result + ((authorityCode == null) ? 0 : authorityCode.hashCode());
    result = prime * result + ((countySpecificCode == null) ? 0 : countySpecificCode.hashCode());
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
    result = prime * result + ((fkasgUnit == null) ? 0 : fkasgUnit.hashCode());
    result = prime * result + ((staffPersonId == null) ? 0 : staffPersonId.hashCode());
    result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
    result = prime * result + ((thirdId == null) ? 0 : thirdId.hashCode());
    result =
        prime * result
            + ((super.getLastUpdatedId() == null) ? 0 : super.getLastUpdatedId().hashCode());
    result =
        prime * result
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
    if (!(obj instanceof StaffUnitAuthority)) {
      return false;
    }
    StaffUnitAuthority other = (StaffUnitAuthority) obj;
    if (authorityCode == null) {
      if (other.authorityCode != null) {
        return false;
      }
    } else if (!authorityCode.equals(other.authorityCode)) {
      return false;
    }
    if (countySpecificCode == null) {
      if (other.countySpecificCode != null) {
        return false;
      }
    } else if (!countySpecificCode.equals(other.countySpecificCode)) {
      return false;
    }
    if (endDate == null) {
      if (other.endDate != null) {
        return false;
      }
    } else if (!endDate.equals(other.endDate)) {
      return false;
    }
    if (fkasgUnit == null) {
      if (other.fkasgUnit != null) {
        return false;
      }
    } else if (!fkasgUnit.equals(other.fkasgUnit)) {
      return false;
    }
    if (staffPersonId == null) {
      if (other.staffPersonId != null) {
        return false;
      }
    } else if (!staffPersonId.equals(other.staffPersonId)) {
      return false;
    }
    if (startDate == null) {
      if (other.startDate != null) {
        return false;
      }
    } else if (!startDate.equals(other.startDate)) {
      return false;
    }
    if (thirdId == null) {
      if (other.thirdId != null) {
        return false;
      }
    } else if (!thirdId.equals(other.thirdId)) {
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
