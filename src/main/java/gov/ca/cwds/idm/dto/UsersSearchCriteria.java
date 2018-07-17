package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UsersSearchCriteria  implements Serializable {

  private static final long serialVersionUID = -7121379778100149474L;

  private Set<String> racfids = new HashSet<>();

  public Set<String> getRacfids() {
    return racfids;
  }

  public void setRacfids(Set<String> racfids) {
    this.racfids = racfids;
  }
}
