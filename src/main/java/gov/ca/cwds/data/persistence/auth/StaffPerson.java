package gov.ca.cwds.data.persistence.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by dmitry.rudenko on 8/21/2017.
 */


@Entity
@Table(name = "STFPERST")
public class StaffPerson {
  @Id
  @Column(name = "IDENTIFIER")
  private String id;

  @Column(name = "FKCWS_OFFT")
  private String cwsOffice;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCwsOffice() {
    return cwsOffice;
  }

  public void setCwsOffice(String cwsOffice) {
    this.cwsOffice = cwsOffice;
  }
}
