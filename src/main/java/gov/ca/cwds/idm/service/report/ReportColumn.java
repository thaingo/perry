package gov.ca.cwds.idm.service.report;

import static gov.ca.cwds.util.Utils.emptyIfNull;

import gov.ca.cwds.idm.dto.User;
import java.util.function.Function;

public class ReportColumn {

  private final String name;

  private final Function<User, String> cellFunction;

  public ReportColumn(String name, Function<User, String> cellFunction) {
    this.name = name;
    this.cellFunction = cellFunction;
  }

  public String getName() {
    return name;
  }

  public String getCellValue(User user) {
    return emptyIfNull(cellFunction.apply(user));
  }
}
