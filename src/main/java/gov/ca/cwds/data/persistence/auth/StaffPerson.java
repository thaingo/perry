package gov.ca.cwds.data.persistence.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by dmitry.rudenko on 8/21/2017.
 */


@Entity
@Table(name = "STFPERST")
@ApiModel
@JsonSnakeCase
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


  @Column(name = "FKCWS_OFFT")
  @JsonIgnore
  private String cwsOffice;

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

  public String getCwsOffice() {
    return cwsOffice;
  }

  public void setCwsOffice(String cwsOffice) {
    this.cwsOffice = cwsOffice;
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
}
