package gov.ca.cwds.service.dto;

import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.data.persistence.auth.UserId;
import gov.ca.cwds.rest.api.domain.auth.StaffAuthorityPrivilege;
import gov.ca.cwds.rest.api.domain.auth.StaffUnitAuthority;

import java.util.Set;

public class CwsUserInfo {

  private UserId user;
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

  public UserId getUser() {
    return user;
  }

  public void setUser(UserId user) {
    this.user = user;
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


  public static final class UserInfoDTOBuilder {
    private boolean socialWorker;
    private UserId user;
    private Set<StaffAuthorityPrivilege> userAuthPrivs;
    private Set<StaffUnitAuthority> staffUnitAuths;
    private StaffPerson staffPerson;
    private CwsOffice cwsOffice;

    private UserInfoDTOBuilder() {
    }

    public static UserInfoDTOBuilder anUserInfoDTO() {
      return new UserInfoDTOBuilder();
    }

    public UserInfoDTOBuilder withSocialWorker(boolean socialWorker) {
      this.socialWorker = socialWorker;
      return this;
    }

    public UserInfoDTOBuilder withUser(UserId user) {
      this.user = user;
      return this;
    }

    public UserInfoDTOBuilder withUserAuthPrivs(Set<StaffAuthorityPrivilege> userAuthPrivs) {
      this.userAuthPrivs = userAuthPrivs;
      return this;
    }

    public UserInfoDTOBuilder withStaffUnitAuths(Set<StaffUnitAuthority> setStaffUnitAuths) {
      this.staffUnitAuths = setStaffUnitAuths;
      return this;
    }

    public UserInfoDTOBuilder withStaffPerson(StaffPerson staffPerson) {
      this.staffPerson = staffPerson;
      return this;
    }

    public UserInfoDTOBuilder withCwsOffice(CwsOffice cwsOffice) {
      this.cwsOffice = cwsOffice;
      return this;
    }

    public CwsUserInfo build() {
      CwsUserInfo cwsUserInfo = new CwsUserInfo();
      cwsUserInfo.setSocialWorker(socialWorker);
      cwsUserInfo.setUser(user);
      cwsUserInfo.setUserAuthPrivs(userAuthPrivs);
      cwsUserInfo.setStaffUnitAuths(staffUnitAuths);
      cwsUserInfo.setStaffPerson(staffPerson);
      cwsUserInfo.setCwsOffice(cwsOffice);
      return cwsUserInfo;
    }
  }
}
