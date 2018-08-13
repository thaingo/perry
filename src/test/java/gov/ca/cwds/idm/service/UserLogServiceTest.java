package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.persistence.model.OperationType.CREATE;
import static gov.ca.cwds.idm.persistence.model.OperationType.UPDATE;
import static gov.ca.cwds.idm.service.UserLogService.filterIdAndOperationList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gov.ca.cwds.idm.dto.UserIdAndOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;

public class UserLogServiceTest {

  @Test
  public void testFilterIdAndOperationList() {

    List<Object[]> pairsList = new ArrayList<>();

    pairsList.add(new Object[]{"a", CREATE});
    pairsList.add(new Object[]{"b", CREATE});
    pairsList.add(new Object[]{"b", UPDATE});
    pairsList.add(new Object[]{"c", UPDATE});

    List<UserIdAndOperation> objectList = filterIdAndOperationList(pairsList);
    assertThat(objectList.size(), is(3));

    Map<String, UserIdAndOperation> testMap = objectList.stream().collect(
        Collectors.toMap(UserIdAndOperation::getId, e -> e));

    assertThat(testMap.size(), is(3));
    assertThat(testMap.get("a").getOperation(), is(CREATE));
    assertThat(testMap.get("b").getOperation(), is(UPDATE));
    assertThat(testMap.get("c").getOperation(), is(UPDATE));
  }
}
