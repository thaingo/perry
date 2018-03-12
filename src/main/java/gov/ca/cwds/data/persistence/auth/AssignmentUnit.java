package gov.ca.cwds.data.persistence.auth;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.data.persistence.cms.CmsPersistentObject;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * {@link CmsPersistentObject} representing an Assignment Unit
 *
 * @author CWDS API Team
 */
@Entity
@Table(name = "ASG_UNIT")
public class AssignmentUnit extends CmsPersistentObject {

  /**
   * Base serialization version. Increment per version of this class.
   */
  private static final long serialVersionUID = 1L;
  @Column(name = "PHONE_NO")
  private Long phoneNo;

  @Column(name = "TEL_EXT_NO")
  private int telExtNo;

  @Column(name = "ASGDSK_IND")
  private String assignmentUnitDeskIndicator;

  @Column(name = "CNTY_SPFCD")
  private String countySpecificCode;

  @Type(type = "date")
  @Column(name = "END_DT")
  private Date endDate;

  @Column(name = "FKCWS_OFFT")
  private String cwsOfficeId;

  @Column(name = "ASGMUNT_NM")
  private String assignmentUnitName;

  @Type(type = "date")
  @Column(name = "START_DT")
  private Date startDate;

  @Id
  @Column(name = "IDENTIFIER")
  private String id;

  /**
   * Default constructor
   * <p>
   * Required for Hibernate
   */
  public AssignmentUnit() {
    super();
  }


  /**
   * @param phoneNo                     the phone number
   * @param telExtNo                    the tel ext number
   * @param assignmentUnitDeskIndicator the assignmentUnitDesk Indicator
   * @param countySpecificCode          the countySpecificCode
   * @param endDate                     the end date
   * @param cwsOfficeId                 the cwsOfficeId
   * @param assignmentUnitName          the assignmentUnitName
   * @param startDate                   the startDate
   * @param id                          the id
   */
  public AssignmentUnit(Long phoneNo, int telExtNo, String assignmentUnitDeskIndicator,
                        String countySpecificCode, Date endDate, String cwsOfficeId, String assignmentUnitName,
                        Date startDate, String id) {
    super();
    this.phoneNo = phoneNo;
    this.telExtNo = telExtNo;
    this.assignmentUnitDeskIndicator = assignmentUnitDeskIndicator;
    this.countySpecificCode = countySpecificCode;
    this.endDate = endDate;
    this.cwsOfficeId = cwsOfficeId;
    this.assignmentUnitName = assignmentUnitName;
    this.startDate = startDate;
    this.id = id;
  }


  /**
   * @return the phoneNo
   */
  public Long getPhoneNo() {
    return phoneNo;
  }


  /**
   * @return the telExtNo
   */
  public int getTelExtNo() {
    return telExtNo;
  }


  /**
   * @return the asgdskInd
   */
  public String getAssignmentUnitDeskIndicator() {
    return assignmentUnitDeskIndicator;
  }


  /**
   * @return the cwsOfficeId
   */
  public String getCwsOfficeId() {
    return cwsOfficeId;
  }


  /**
   * @return the assignmentNumber
   */
  public String getAssignmentUnitName() {
    return assignmentUnitName;
  }


  /**
   * @return the id
   */
  public String getId() {
    return id;
  }


  /**
   * {@inheritDoc}
   *
   * @see gov.ca.cwds.data.persistence.PersistentObject#getPrimaryKey()
   */
  @Override
  public String getPrimaryKey() {
    return getId();
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
   * @return the startDate
   */
  public Date getStartDate() {
    return startDate;
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
    result =
            prime * result
                    + ((assignmentUnitDeskIndicator == null) ? 0 : assignmentUnitDeskIndicator.hashCode());
    result = prime * result + ((assignmentUnitName == null) ? 0 : assignmentUnitName.hashCode());
    result = prime * result + ((countySpecificCode == null) ? 0 : countySpecificCode.hashCode());
    result = prime * result + ((cwsOfficeId == null) ? 0 : cwsOfficeId.hashCode());
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((phoneNo == null) ? 0 : phoneNo.hashCode());
    result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
    result = prime * result + telExtNo;
    return result;
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  @SuppressFBWarnings("MDM_BIGDECIMAL_EQUALS") //BigDecimal.equals used for equals/hashCode only
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
    AssignmentUnit other = (AssignmentUnit) obj;
    if (assignmentUnitDeskIndicator == null) {
      if (other.assignmentUnitDeskIndicator != null) {
        return false;
      }
    } else if (!assignmentUnitDeskIndicator.equals(other.assignmentUnitDeskIndicator)) {
      return false;
    }
    if (assignmentUnitName == null) {
      if (other.assignmentUnitName != null) {
        return false;
      }
    } else if (!assignmentUnitName.equals(other.assignmentUnitName)) {
      return false;
    }
    if (countySpecificCode == null) {
      if (other.countySpecificCode != null) {
        return false;
      }
    } else if (!countySpecificCode.equals(other.countySpecificCode)) {
      return false;
    }
    if (cwsOfficeId == null) {
      if (other.cwsOfficeId != null) {
        return false;
      }
    } else if (!cwsOfficeId.equals(other.cwsOfficeId)) {
      return false;
    }
    if (endDate == null) {
      if (other.endDate != null) {
        return false;
      }
    } else if (!endDate.equals(other.endDate)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (phoneNo == null) {
      if (other.phoneNo != null) {
        return false;
      }
    } else if (!phoneNo.equals(other.phoneNo)) {
      return false;
    }
    if (startDate == null) {
      if (other.startDate != null) {
        return false;
      }
    } else if (!startDate.equals(other.startDate)) {
      return false;
    }
    if (telExtNo != other.telExtNo) {
      return false;
    }
    return true;
  }

}
