package gov.ca.cwds.idm.persistence.ns.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "audit_event")
public class NsAuditEvent implements Serializable {

  private static final long serialVersionUID = 5334981920633133553L;

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "event_timestamp")
  private LocalDateTime eventTimestamp;

  @Column(name = "event_json")
  private String auditEvent;

  @Column(name = "processed")
  private boolean processed;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getEventTimestamp() {
    return eventTimestamp;
  }

  public void setEventTimestamp(LocalDateTime eventTimestamp) {
    this.eventTimestamp = eventTimestamp;
  }

  public String getAuditEvent() {
    return auditEvent;
  }

  public void setAuditEvent(String auditEvent) {
    this.auditEvent = auditEvent;
  }

  public boolean isProcessed() {
    return processed;
  }

  public void setProcessed(boolean processed) {
    this.processed = processed;
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
