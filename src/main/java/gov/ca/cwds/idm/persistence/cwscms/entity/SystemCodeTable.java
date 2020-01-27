package gov.ca.cwds.idm.persistence.cwscms.entity;

import java.sql.Timestamp;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;

@Entity
@DiscriminatorColumn(name = "FKS_META_T")
@Table(name = "SYS_CD_C")
public class SystemCodeTable {

  private static final long serialVersionUID = -3979949426929339075L;

  @Id
  @Column(name = "SYS_ID")
  @Access(AccessType.PROPERTY)//to get id without fetching entire System Code entity
  private Short systemId;

  @Column(name = "CATEGORYID")
  private Short categoryId;

  @Type(type = "yes_no")
  @Column(name = "INACTV_IND")
  private boolean inactiveIndicator;

  @Column(name = "OTHER_CD")
  @ColumnTransformer(read = "trim(OTHER_CD)")
  private String otherCode;

  @Column(name = "SHORT_DSC")
  @ColumnTransformer(read = "trim(SHORT_DSC)")
  private String shortDescription;

  @Column(name = "LGC_ID")
  @ColumnTransformer(read = "trim(LGC_ID)")
  private String logicalId;

  @Column(name = "LST_UPD_ID")
  private String lastUpdatedId;

  @Column(name = "LST_UPD_TS")
  private Timestamp lastUpdatedTime;

  @Column(name = "THIRD_ID")
  private String thirdId;

  @Column(name = "LONG_DSC")
  @ColumnTransformer(read = "trim(LONG_DSC)")
  private String longDescription;

  @Column(name = "FKS_META_T", insertable = false, updatable = false)
  private String fkMeta;

  public Short getSystemId() {
    return systemId;
  }

  public void setSystemId(Short sysId) {
    this.systemId = sysId;
  }

  public Short getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Short categoryid) {
    this.categoryId = categoryid;
  }

  public boolean getInactiveIndicator() {
    return inactiveIndicator;
  }

  public void setInactiveIndicator(boolean inactvInd) {
    this.inactiveIndicator = inactvInd;
  }

  public String getOtherCode() {
    return otherCode;
  }

  public void setOtherCode(String otherCd) {
    this.otherCode = otherCd;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDsc) {
    this.shortDescription = shortDsc;
  }

  public String getLogicalId() {
    return logicalId;
  }

  public void setLogicalId(String lgcId) {
    this.logicalId = lgcId;
  }

  public String getLastUpdatedId() {
    return lastUpdatedId;
  }

  public void setLastUpdatedId(String lstUpdId) {
    this.lastUpdatedId = lstUpdId;
  }

  public Timestamp getLastUpdatedTime() {
    return lastUpdatedTime;
  }

  public void setLastUpdatedTime(Timestamp lstUpdTs) {
    this.lastUpdatedTime = lstUpdTs;
  }

  public String getThirdId() {
    return thirdId;
  }

  public void setThirdId(String thirdId) {
    this.thirdId = thirdId;
  }

  public String getFkMeta() {
    return fkMeta;
  }

  public void setFkMeta(String fksMetaT) {
    this.fkMeta = fksMetaT;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(String longDsc) {
    this.longDescription = longDsc;
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
