package gov.ca.cwds.data.persistence.auth;

import gov.ca.cwds.data.persistence.cms.CmsPersistentObject;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * {@link CmsPersistentObject} representing a CWS Office.
 *
 * @author CWDS API Team
 */

@Entity
@Table(name = "CWS_OFFT")
public class CwsOffice extends CmsPersistentObject {

  /**
   * Base serialization version. Increment per version of this class.
   */
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "IDENTIFIER")
  private String officeId;

  @Column(name = "FAX_NO")
  private Long faxNumber;

  @Column(name = "GEO_RGNTCD")
  private String geographicRegionTextCode;

  @Type(type = "short")
  @Column(name = "GVR_ENTC")
  private Short governmentEntityType;

  @Column(name = "HDQRTR_IND")
  private String headquarterIndicator;

  @Column(name = "INACTV_IND")
  private String inactiveIndicator;

  @Column(name = "MAILST_DSC")
  private String mailStopDescription;

  @Column(name = "MSG_TEL_NO")
  private Long messagePhoneNumber;

  @Type(type = "integer")
  @Column(name = "MSG_EXT_NO")
  private Integer messagePhoneExtensionNumber;

  @Column(name = "CWS_OFF_NO")
  private String cwsOffNumber;

  @Column(name = "PRM_TEL_NO")
  private Long primaryPhoneNumber;

  @Type(type = "integer")
  @Column(name = "PRM_EXT_NO")
  private Integer primaryPhoneExtensionNumber;

  @Column(name = "FKSTFPERST")
  private String staffPersonId;

  @Column(name = "COMNT_DSC")
  private String commentDescription;

  @Column(name = "AGENCY_NM")
  private String agencyName;

  @Column(name = "DPT_DIV_NM")
  private String departmentDivisionName;

  @Column(name = "CWS_OFF_NM")
  private String cwsOfficeName;

  @Column(name = "CNTY_SPFCD")
  private String countySpecificCode;

  @Type(type = "short")
  @Column(name = "AGCY_CD_NO")
  private Short agencyCodeNumber;

  @Type(type = "short")
  @Column(name = "LOC_CNTY")
  private Short locationCountyType;

  @Column(name = "DIR_NM_TL")
  private String directorsNameTitle;

  /**
   * Default constructor
   * <p>
   * Required for Hibernate
   */
  public CwsOffice() {
    super();
  }

  /**
   * @param officeId                    the primary key
   * @param faxNumber                   the faxNumber
   * @param geographicRegionTextCode    geographicRegionTextCode
   * @param governmentEntityType        governmentEntityType
   * @param headquarterIndicator        headquarterIndicator
   * @param inactiveIndicator           inactiveIndicator
   * @param mailStopDescription         mailStopDescription
   * @param messagePhoneNumber          messagePhoneNumber
   * @param messagePhoneExtensionNumber the messagePhoneExtensionNumber
   * @param cwsOffNumber                the cwsOffNumber
   * @param primaryPhoneNumber          the primaryPhoneNumber
   * @param primaryPhoneExtensionNumber the primaryPhoneExtensionNumber
   * @param staffPersonId               the staffPersonId
   * @param commentDescription          the commentDescription
   * @param agencyName                  the agencyName
   * @param departmentDivisionName      the departmentDivisionName
   * @param cwsOfficeName               the cwsOfficeName
   * @param countySpecificCode          the countySpecificCode
   * @param agencyCodeNumber            the agencyCodeNumber
   * @param locationCountyType          the locationCountyType
   * @param directorsNameTitle          the directorsNameTitle
   */
  public CwsOffice(String officeId, Long faxNumber, String geographicRegionTextCode,
                   Short governmentEntityType, String headquarterIndicator, String inactiveIndicator,
                   String mailStopDescription, Long messagePhoneNumber,
                   Integer messagePhoneExtensionNumber, String cwsOffNumber, Long primaryPhoneNumber,
                   Integer primaryPhoneExtensionNumber, String staffPersonId, String commentDescription,
                   String agencyName, String departmentDivisionName, String cwsOfficeName,
                   String countySpecificCode, Short agencyCodeNumber, Short locationCountyType,
                   String directorsNameTitle) {
    super();
    this.officeId = officeId;
    this.faxNumber = faxNumber;
    this.geographicRegionTextCode = geographicRegionTextCode;
    this.governmentEntityType = governmentEntityType;
    this.headquarterIndicator = headquarterIndicator;
    this.inactiveIndicator = inactiveIndicator;
    this.mailStopDescription = mailStopDescription;
    this.messagePhoneNumber = messagePhoneNumber;
    this.messagePhoneExtensionNumber = messagePhoneExtensionNumber;
    this.cwsOffNumber = cwsOffNumber;
    this.primaryPhoneNumber = primaryPhoneNumber;
    this.primaryPhoneExtensionNumber = primaryPhoneExtensionNumber;
    this.staffPersonId = staffPersonId;
    this.commentDescription = commentDescription;
    this.agencyName = agencyName;
    this.departmentDivisionName = departmentDivisionName;
    this.cwsOfficeName = cwsOfficeName;
    this.countySpecificCode = countySpecificCode;
    this.agencyCodeNumber = agencyCodeNumber;
    this.locationCountyType = locationCountyType;
    this.directorsNameTitle = directorsNameTitle;
  }


  @Override
  public Serializable getPrimaryKey() {
    return getOfficeId();
  }

  /**
   * @return the officeId
   */
  public String getOfficeId() {
    return officeId;
  }

  /**
   * @return the faxNumber
   */
  public Long getFaxNumber() {
    return faxNumber;
  }

  /**
   * @return the geographicRegionTextCode
   */
  public String getGeographicRegionTextCode() {
    return geographicRegionTextCode;
  }

  /**
   * @return the governmentEntityType
   */
  public Short getGovernmentEntityType() {
    return governmentEntityType;
  }

  /**
   * @return the headquarterIndicator
   */
  public String getHeadquarterIndicator() {
    return headquarterIndicator;
  }

  /**
   * @return the inactiveIndicator
   */
  public String getInactiveIndicator() {
    return inactiveIndicator;
  }

  /**
   * @return the mailStopDescription
   */
  public String getMailStopDescription() {
    return mailStopDescription;
  }

  /**
   * @return the messagePhoneNumber
   */
  public Long getMessagePhoneNumber() {
    return messagePhoneNumber;
  }

  /**
   * @return the messagePhoneExtensionNumber
   */
  public Integer getMessagePhoneExtensionNumber() {
    return messagePhoneExtensionNumber;
  }

  /**
   * @return the cwsOffNumber
   */
  public String getCwsOffNumber() {
    return cwsOffNumber;
  }

  /**
   * @return the primaryPhoneNumber
   */
  public Long getPrimaryPhoneNumber() {
    return primaryPhoneNumber;
  }

  /**
   * @return the primaryPhoneExtensionNumber
   */
  public Integer getPrimaryPhoneExtensionNumber() {
    return primaryPhoneExtensionNumber;
  }

  /**
   * @return the staffPersonId
   */
  public String getStaffPersonId() {
    return staffPersonId;
  }

  /**
   * @return the commentDescription
   */
  public String getCommentDescription() {
    return commentDescription;
  }

  /**
   * @return the agencyName
   */
  public String getAgencyName() {
    return agencyName;
  }

  /**
   * @return the departmentDivisionName
   */
  public String getDepartmentDivisionName() {
    return departmentDivisionName;
  }

  /**
   * @return the cwsOfficeName
   */
  public String getCwsOfficeName() {
    return cwsOfficeName;
  }

  /**
   * @return the countySpecificCode
   */
  public String getCountySpecificCode() {
    return countySpecificCode;
  }

  /**
   * @return the agencyCodeNumber
   */
  public Short getAgencyCodeNumber() {
    return agencyCodeNumber;
  }

  /**
   * @return the locationCountyType
   */
  public Short getLocationCountyType() {
    return locationCountyType;
  }

  /**
   * @return the directorsNameTitle
   */
  public String getDirectorsNameTitle() {
    return directorsNameTitle;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((agencyCodeNumber == null) ? 0 : agencyCodeNumber.hashCode());
    result = prime * result + ((agencyName == null) ? 0 : agencyName.hashCode());
    result = prime * result + ((commentDescription == null) ? 0 : commentDescription.hashCode());
    result = prime * result + ((countySpecificCode == null) ? 0 : countySpecificCode.hashCode());
    result = prime * result + ((cwsOffNumber == null) ? 0 : cwsOffNumber.hashCode());
    result = prime * result + ((cwsOfficeName == null) ? 0 : cwsOfficeName.hashCode());
    result =
            prime * result + ((departmentDivisionName == null) ? 0 : departmentDivisionName.hashCode());
    result = prime * result + ((directorsNameTitle == null) ? 0 : directorsNameTitle.hashCode());
    result = prime * result + ((faxNumber == null) ? 0 : faxNumber.hashCode());
    result =
            prime * result
                    + ((geographicRegionTextCode == null) ? 0 : geographicRegionTextCode.hashCode());
    result =
            prime * result + ((governmentEntityType == null) ? 0 : governmentEntityType.hashCode());
    result =
            prime * result + ((headquarterIndicator == null) ? 0 : headquarterIndicator.hashCode());
    result = prime * result + ((inactiveIndicator == null) ? 0 : inactiveIndicator.hashCode());
    result = prime * result + ((locationCountyType == null) ? 0 : locationCountyType.hashCode());
    result = prime * result + ((mailStopDescription == null) ? 0 : mailStopDescription.hashCode());
    result =
            prime * result
                    + ((messagePhoneExtensionNumber == null) ? 0 : messagePhoneExtensionNumber.hashCode());
    result = prime * result + ((messagePhoneNumber == null) ? 0 : messagePhoneNumber.hashCode());
    result = prime * result + ((officeId == null) ? 0 : officeId.hashCode());
    result =
            prime * result
                    + ((primaryPhoneExtensionNumber == null) ? 0 : primaryPhoneExtensionNumber.hashCode());
    result = prime * result + ((primaryPhoneNumber == null) ? 0 : primaryPhoneNumber.hashCode());
    result = prime * result + ((staffPersonId == null) ? 0 : staffPersonId.hashCode());
    result =
            prime * result
                    + ((super.getLastUpdatedId() == null) ? 0 : super.getLastUpdatedId().hashCode());
    result =
            prime * result
                    + ((super.getLastUpdatedTime() == null) ? 0 : super.getLastUpdatedTime().hashCode());

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CwsOffice)) {
      return false;
    }
    CwsOffice other = (CwsOffice) obj;
    if (agencyCodeNumber == null) {
      if (other.agencyCodeNumber != null) {
        return false;
      }
    } else if (!agencyCodeNumber.equals(other.agencyCodeNumber)) {
      return false;
    }
    if (agencyName == null) {
      if (other.agencyName != null) {
        return false;
      }
    } else if (!agencyName.equals(other.agencyName)) {
      return false;
    }
    if (commentDescription == null) {
      if (other.commentDescription != null) {
        return false;
      }
    } else if (!commentDescription.equals(other.commentDescription)) {
      return false;
    }
    if (countySpecificCode == null) {
      if (other.countySpecificCode != null) {
        return false;
      }
    } else if (!countySpecificCode.equals(other.countySpecificCode)) {
      return false;
    }
    if (cwsOffNumber == null) {
      if (other.cwsOffNumber != null) {
        return false;
      }
    } else if (!cwsOffNumber.equals(other.cwsOffNumber)) {
      return false;
    }
    if (cwsOfficeName == null) {
      if (other.cwsOfficeName != null) {
        return false;
      }
    } else if (!cwsOfficeName.equals(other.cwsOfficeName)) {
      return false;
    }
    if (departmentDivisionName == null) {
      if (other.departmentDivisionName != null) {
        return false;
      }
    } else if (!departmentDivisionName.equals(other.departmentDivisionName)) {
      return false;
    }
    if (directorsNameTitle == null) {
      if (other.directorsNameTitle != null) {
        return false;
      }
    } else if (!directorsNameTitle.equals(other.directorsNameTitle)) {
      return false;
    }
    if (faxNumber == null) {
      if (other.faxNumber != null) {
        return false;
      }
    } else if (!faxNumber.equals(other.faxNumber)) {
      return false;
    }
    if (geographicRegionTextCode == null) {
      if (other.geographicRegionTextCode != null) {
        return false;
      }
    } else if (!geographicRegionTextCode.equals(other.geographicRegionTextCode)) {
      return false;
    }
    if (governmentEntityType == null) {
      if (other.governmentEntityType != null) {
        return false;
      }
    } else if (!governmentEntityType.equals(other.governmentEntityType)) {
      return false;
    }
    if (headquarterIndicator == null) {
      if (other.headquarterIndicator != null) {
        return false;
      }
    } else if (!headquarterIndicator.equals(other.headquarterIndicator)) {
      return false;
    }
    if (inactiveIndicator == null) {
      if (other.inactiveIndicator != null) {
        return false;
      }
    } else if (!inactiveIndicator.equals(other.inactiveIndicator)) {
      return false;
    }
    if (locationCountyType == null) {
      if (other.locationCountyType != null) {
        return false;
      }
    } else if (!locationCountyType.equals(other.locationCountyType)) {
      return false;
    }
    if (mailStopDescription == null) {
      if (other.mailStopDescription != null) {
        return false;
      }
    } else if (!mailStopDescription.equals(other.mailStopDescription)) {
      return false;
    }
    if (messagePhoneExtensionNumber == null) {
      if (other.messagePhoneExtensionNumber != null) {
        return false;
      }
    } else if (!messagePhoneExtensionNumber.equals(other.messagePhoneExtensionNumber)) {
      return false;
    }
    if (messagePhoneNumber == null) {
      if (other.messagePhoneNumber != null) {
        return false;
      }
    } else if (!messagePhoneNumber.equals(other.messagePhoneNumber)) {
      return false;
    }
    if (officeId == null) {
      if (other.officeId != null) {
        return false;
      }
    } else if (!officeId.equals(other.officeId)) {
      return false;
    }
    if (primaryPhoneExtensionNumber == null) {
      if (other.primaryPhoneExtensionNumber != null) {
        return false;
      }
    } else if (!primaryPhoneExtensionNumber.equals(other.primaryPhoneExtensionNumber)) {
      return false;
    }
    if (primaryPhoneNumber == null) {
      if (other.primaryPhoneNumber != null) {
        return false;
      }
    } else if (!primaryPhoneNumber.equals(other.primaryPhoneNumber)) {
      return false;
    }
    if (staffPersonId == null) {
      if (other.staffPersonId != null) {
        return false;
      }
    } else if (!staffPersonId.equals(other.staffPersonId)) {
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
