package gov.ca.cwds.dto.app;

import static gov.ca.cwds.util.Utils.isStatusHealthy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.boot.actuate.health.Health;

/**
 * DTO for HealthCheck status.
 *
 * @author CWDS TPT-2
 */
@JsonPropertyOrder({"healthy", "message", "error", "details", "timestamp"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
public class HealthCheckResultDto {

  private boolean healthy;
  private String message;
  private String error;
  private Map<String, Object> details;
  private String timestamp;

  /**
   * default constructor
   */
  public HealthCheckResultDto() {
    // no-op
  }

  /**
   * Convenient constructor to instantiate from a Health instance.
   *
   * @param health a Health instance
   * @param timestamp a string with timestamp
   */
  public HealthCheckResultDto(Health health, String timestamp) {
    this.healthy = isStatusHealthy(health.getStatus());
    this.message = health.getStatus().getDescription();
    this.details = health.getDetails();
    if (this.details.containsKey("error")) {
      this.error = this.details.get("error").toString();
    }
    this.timestamp = timestamp;
  }

  public boolean isHealthy() {
    return healthy;
  }

  public void setHealthy(boolean healthy) {
    this.healthy = healthy;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public Map<String, Object> getDetails() {
    return details;
  }

  public void setDetails(Map<String, Object> details) {
    this.details = details;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

  @Override
  public boolean equals(Object object) {
    return EqualsBuilder.reflectionEquals(this, object);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}
