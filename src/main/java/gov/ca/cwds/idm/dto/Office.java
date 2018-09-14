package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Office implements Serializable {

  private static final long serialVersionUID = 8936598108771141372L;

  private String officeId;
  private String officeName;
  private short countyId;
  private String countyName;

  public Office(String officeId, String officeName, short countyId, String countyName) {
    this.officeId = officeId;
    this.officeName = officeName;
    this.countyId = countyId;
    this.countyName = countyName;
  }

  public String getOfficeId() {
    return officeId;
  }

  public void setOfficeId(String officeId) {
    this.officeId = officeId;
  }

  public short getCountyId() {
    return countyId;
  }

  public void setCountyId(short countyId) {
    this.countyId = countyId;
  }

  public String getCountyName() {
    return countyName;
  }

  public void setCountyName(String countyName) {
    this.countyName = countyName;
  }

  public String getOfficeName() {
    return officeName;
  }

  public void setOfficeName(String officeName) {
    this.officeName = officeName;
  }
}
