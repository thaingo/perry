package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.persistence.model.OperationType.CREATE;
import static gov.ca.cwds.idm.persistence.model.OperationType.UPDATE;
import static gov.ca.cwds.idm.service.UserLogService.getIdAndOperationMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gov.ca.cwds.idm.persistence.model.OperationType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class UserLogServiceTest {

  @Test
  public void testGetIdAndOperationMap() {

    List<Object[]> pairsList = new ArrayList<>();

    pairsList.add(new Object[]{"a", CREATE});
    pairsList.add(new Object[]{"b", CREATE});
    pairsList.add(new Object[]{"b", UPDATE});
    pairsList.add(new Object[]{"c", UPDATE});

    Map<String, OperationType> pairsMap = getIdAndOperationMap(pairsList);
    assertThat(pairsMap.size(), is(3));
    assertThat(pairsMap.get("a"), is(CREATE));
    assertThat(pairsMap.get("b"), is(CREATE));
    assertThat(pairsMap.get("c"), is(UPDATE));
  }
}
