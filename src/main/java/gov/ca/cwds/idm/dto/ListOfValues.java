package gov.ca.cwds.idm.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ListOfValues implements Serializable {

  private static final long serialVersionUID = -7212877645584842752L;

  private boolean editable;
  private List<String> possibleValues;

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public List<String> getPossibleValues() {
    return possibleValues;
  }

  public void setPossibleValues(List<String> possibleValues) {
    this.possibleValues = possibleValues;
  }
}
