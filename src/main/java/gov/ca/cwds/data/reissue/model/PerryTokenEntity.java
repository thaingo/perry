package gov.ca.cwds.data.reissue.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Created by TPT2 on 10/24/2017.
 */
@Entity
@Table(name = "perry_token")
public class PerryTokenEntity implements Serializable {
  @Id
  @Column(name = "token", nullable = false)
  private String token;
  @Column(name = "access_code")
  private String accessCode;
  @Column(name = "user_id", nullable = false)
  private String user;
  @Column(name = "sso_token", length = 20000, nullable = false)
  private String ssoToken;
  @Column(name = "json_token", nullable = false)
  private String jsonToken;
  @Column(name = "created_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate = new Date();
  @Column(name = "security_context", length = 20000, nullable = false)
  private byte[] securityContext;
  @Column(name = "last_idp_validate_time")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastIdpValidateTime;

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getSsoToken() {
    return ssoToken;
  }

  public void setSsoToken(String ssoToken) {
    this.ssoToken = ssoToken;
  }

  public String getJsonToken() {
    return jsonToken;
  }

  public void setJsonToken(String jsonToken) {
    this.jsonToken = jsonToken;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getAccessCode() {
    return accessCode;
  }

  public void setAccessCode(String accessCode) {
    this.accessCode = accessCode;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public byte[] getSecurityContext() {
    return securityContext;
  }

  public void setSecurityContext(byte[] securityContext) {
    this.securityContext = securityContext;
  }

  public Date getLastIdpValidateTime() {
    return lastIdpValidateTime;
  }

  public void setLastIdpValidateTime(Date lastIdpValidateTime) {
    this.lastIdpValidateTime = lastIdpValidateTime;
  }

  public static <T> T getSecurityContext(PerryTokenEntity perryTokenEntity) {
    return SerializationUtils.deserialize(perryTokenEntity.getSecurityContext());
  }
}
