package gov.ca.cwds.data.persistence.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.data.converter.StringToRequiredIntegerConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Where;

/** Created by dmitry.rudenko on 8/21/2017. */
@Entity
@Table(name = "STFPERST")
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StaffPerson {

  @Id
  @Column(name = "IDENTIFIER")
  @ApiModelProperty(example = "q12")
  @JsonProperty("id")
  private String id;

  @Column(name = "CNTY_SPFCD")
  @ApiModelProperty(example = "12")
  @JsonProperty("county_code")
  private String countyCode;

  @Column(name = "FIRST_NM")
  @ApiModelProperty(example = "John")
  @JsonProperty("first_name")
  private String firstName;

  @Column(name = "LAST_NM")
  @ApiModelProperty(example = "Smith")
  @JsonProperty("last_name")
  private String lastName;

  @Column(name = "END_DT")
  private LocalDate endDate;

  @Column(name = "START_DT")
  private LocalDate startDate;

  @Column(name = "PHONE_NO")
  @JsonIgnore
  private Long phoneNumber;

  @Column(name = "TEL_EXT_NO")
  @JsonIgnore
  @Convert(converter = StringToRequiredIntegerConverter.class)
  private String phoneExtensionNumber;

  @ManyToOne
  @JoinColumn(name = "FKCWS_OFFT", insertable = false, updatable = false)
  private CwsOffice office;

  @OneToMany
  @JoinColumn(name = "FKSTFPERST")
  @Where(clause = "END_DT IS NULL")
  private Set<StaffUnitAuthority> unitAuthorities;

  public CwsOffice getOffice() {
    return office;
  }

  public void setOffice(CwsOffice office) {
    this.office = office;
  }

  public Set<StaffUnitAuthority> getUnitAuthorities() {
    return unitAuthorities;
  }

  public void setUnitAuthorities(Set<StaffUnitAuthority> unitAuthorities) {
    this.unitAuthorities = unitAuthorities;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCountyCode() {
    return countyCode;
  }

  public void setCountyCode(String countyCode) {
    this.countyCode = countyCode;
  }

  public String getFirstName() {
    return StringUtils.trimToEmpty(firstName);
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return StringUtils.trimToEmpty(lastName);
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public Long getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(Long phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPhoneExtensionNumber() {
    return phoneExtensionNumber;
  }

  public void setPhoneExtensionNumber(String phoneExtensionNumber) {
    this.phoneExtensionNumber = phoneExtensionNumber;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof StaffPerson)) {
      return false;
    }

    StaffPerson that = (StaffPerson) o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(countyCode, that.countyCode)
        .append(firstName, that.firstName)
        .append(lastName, that.lastName)
        .append(endDate, that.endDate)
        .append(startDate, that.startDate)
        .append(office, that.office)
        .append(unitAuthorities, that.unitAuthorities)
        .append(phoneNumber, that.phoneNumber)
        .append(phoneExtensionNumber, that.phoneExtensionNumber)
        .isEquals();
  }

  @Override
  public final int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(id)
        .append(countyCode)
        .append(firstName)
        .append(lastName)
        .append(endDate)
        .append(startDate)
        .append(office)
        .append(unitAuthorities)
        .append(phoneNumber)
        .append(phoneExtensionNumber)
        .toHashCode();
  }
}
