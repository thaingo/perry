package gov.ca.cwds.data.persistence.auth;

import gov.ca.cwds.data.persistence.cms.CmsPersistentObject;
import java.util.Date;
import java.util.Objects;
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

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StaffUnitAuthority)) {
      return false;
    }
    StaffUnitAuthority that = (StaffUnitAuthority) o;
    return Objects.equals(getAuthorityCode(), that.getAuthorityCode()) &&
        Objects.equals(getCountySpecificCode(), that.getCountySpecificCode()) &&
        Objects.equals(getEndDate(), that.getEndDate()) &&
        Objects.equals(getFkasgUnit(), that.getFkasgUnit()) &&
        Objects.equals(getStaffPersonId(), that.getStaffPersonId()) &&
        Objects.equals(getStartDate(), that.getStartDate()) &&
        Objects.equals(getThirdId(), that.getThirdId()) &&
        Objects.equals(getAssignmentUnit(), that.getAssignmentUnit()) &&
        Objects.equals(getLastUpdatedTime(), that.getLastUpdatedTime()) &&
        Objects.equals(getLastUpdatedId(), that.getLastUpdatedId());
  }

  @Override
  public final int hashCode() {

    return Objects.hash(getAuthorityCode(), getCountySpecificCode(), getEndDate(), getFkasgUnit(),
        getStaffPersonId(), getStartDate(), getThirdId(), getAssignmentUnit(),
        getLastUpdatedTime(), getLastUpdatedId());
  }
}
