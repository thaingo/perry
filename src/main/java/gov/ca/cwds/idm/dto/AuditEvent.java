package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.time.LocalDateTime;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AuditEvent<T> implements Serializable {

  private static final long serialVersionUID = 2540746831769360406L;

  private String id;

  private String userLogin;
  private T event;
  private String comment;
  private String eventType;
  private String eventSource;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime timestamp;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserLogin() {
    return userLogin;
  }

  public void setUserLogin(String userLogin) {
    this.userLogin = userLogin;
  }

  public T getEvent() {
    return event;
  }

  public void setEvent(T event) {
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
  
}
