package gov.ca.cwds.idm.event;

import static gov.ca.cwds.util.Utils.DATE_TIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public abstract class AuditEvent<T extends Serializable> implements Serializable {

  private static final long serialVersionUID = 7641668299790997287L;

  @JsonIgnore
  private String id;
  private String userLogin;
  private T event;
  private String comment;
  private String eventType;
  private String eventSource;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
  private LocalDateTime timestamp;

  public String getUserLogin() {
    return userLogin;
  }

  public void setUserLogin(String userLogin) {
    this.userLogin = userLogin;
  }

  public T getEvent() {
    return event;
  }

  public final void setEvent(T event) {
    this.event = event;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getEventSource() {
    return eventSource;
  }

  public void setEventSource(String eventSource) {
    this.eventSource = eventSource;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AuditEvent<?> that = (AuditEvent<?>) o;

    return new EqualsBuilder()
        .append(userLogin, that.userLogin)
        .append(event, that.event)
        .append(comment, that.comment)
        .append(eventType, that.eventType)
        .append(eventSource, that.eventSource)
        .append(timestamp, that.timestamp)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(userLogin)
        .append(event)
        .append(comment)
        .append(eventType)
        .append(eventSource)
        .append(timestamp)
        .toHashCode();
  }
}
