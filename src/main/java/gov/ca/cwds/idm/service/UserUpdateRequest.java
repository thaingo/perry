package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.event.AuditEvent;
import gov.ca.cwds.idm.service.diff.UpdateDifference;
import java.util.ArrayList;
import java.util.List;


public class UserUpdateRequest {

  private String userId;
  private User existedUser;
  private UpdateDifference updateDifference;
  private List<AuditEvent> auditEvents = new ArrayList<>();

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setExistedUser(User existedUser) {
    this.existedUser = existedUser;
  }

  public User getExistedUser() {
    return existedUser;
  }

  public UpdateDifference getUpdateDifference() {
    return updateDifference;
  }

  public void setUpdateDifference(UpdateDifference updateDifference) {
    this.updateDifference = updateDifference;
  }

  public void addAuditEvent(AuditEvent auditEvent) {
    this.auditEvents.add(auditEvent);
  }

  public void addAuditEvents(List<AuditEvent> auditEvent) {
    this.auditEvents.addAll(auditEvent);
  }

  public List<AuditEvent> getAuditEvents() {
    return auditEvents;
  }

}
