package gov.ca.cwds.service.dto;

import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.rest.api.domain.auth.StaffAuthorityPrivilege;
import gov.ca.cwds.rest.api.domain.auth.StaffUnitAuthority;

import java.util.Set;

public class CwsUserInfo {

  private String racfId;
  private boolean socialWorker;
  private Set<StaffAuthorityPrivilege> userAuthPrivs;
  private Set<StaffUnitAuthority> staffUnitAuths;
  private StaffPerson staffPerson;
  private CwsOffice cwsOffice;

  public boolean isSocialWorker() {
    return socialWorker;
  }

  public void setSocialWorker(boolean socialWorker) {
    this.socialWorker = socialWorker;
  }

  public Set<StaffAuthorityPrivilege> getUserAuthPrivs() {
    return userAuthPrivs;
  }

  public void setUserAuthPrivs(Set<StaffAuthorityPrivilege> userAuthPrivs) {
    this.userAuthPrivs = userAuthPrivs;
  }

  public Set<StaffUnitAuthority> getStaffUnitAuths() {
    return staffUnitAuths;
  }

  public void setStaffUnitAuths(Set<StaffUnitAuthority> staffUnitAuths) {
    this.staffUnitAuths = staffUnitAuths;
  }

  public StaffPerson getStaffPerson() {
    return staffPerson;
  }

  public void setStaffPerson(StaffPerson staffPerson) {
    this.staffPerson = staffPerson;
  }

  public CwsOffice getCwsOffice() {
    return cwsOffice;
  }

  public void setCwsOffice(CwsOffice cwsOffice) {
    this.cwsOffice = cwsOffice;
  }

  public String getRacfId() {
    return racfId;
  }

  public void setRacfId(String racfId) {
    this.racfId = racfId;
  }

  public static final class CwsUserInfoBuilder {
    private String racfId;
    private boolean socialWorker;
    private Set<StaffAuthorityPrivilege> userAuthPrivs;
    private Set<StaffUnitAuthority> staffUnitAuths;
    private StaffPerson staffPerson;
    private CwsOffice cwsOffice;

    private CwsUserInfoBuilder() {}

    public static CwsUserInfoBuilder aCwsUserInfo() {
      return new CwsUserInfoBuilder();
    }

    public CwsUserInfoBuilder withRacfId(String racfId) {
      this.racfId = racfId;
      return this;
    }

    public CwsUserInfoBuilder withSocialWorker(boolean socialWorker) {
      this.socialWorker = socialWorker;
      return this;
    }

    public CwsUserInfoBuilder withUserAuthPrivs(Set<StaffAuthorityPrivilege> userAuthPrivs) {
      this.userAuthPrivs = userAuthPrivs;
      return this;
    }

    public CwsUserInfoBuilder withStaffUnitAuths(Set<StaffUnitAuthority> staffUnitAuths) {
      this.staffUnitAuths = staffUnitAuths;
      return this;
    }

    public CwsUserInfoBuilder withStaffPerson(StaffPerson staffPerson) {
      this.staffPerson = staffPerson;
      return this;
    }

    public CwsUserInfoBuilder withCwsOffice(CwsOffice cwsOffice) {
      this.cwsOffice = cwsOffice;
      return this;
    }

    public CwsUserInfo build() {
      CwsUserInfo cwsUserInfo = new CwsUserInfo();
      cwsUserInfo.setRacfId(racfId);
      cwsUserInfo.setSocialWorker(socialWorker);
      cwsUserInfo.setUserAuthPrivs(userAuthPrivs);
      cwsUserInfo.setStaffUnitAuths(staffUnitAuths);
      cwsUserInfo.setStaffPerson(staffPerson);
      cwsUserInfo.setCwsOffice(cwsOffice);
      return cwsUserInfo;
    }
  }
}
