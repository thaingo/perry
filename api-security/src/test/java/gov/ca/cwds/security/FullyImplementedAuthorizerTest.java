package gov.ca.cwds.security;

import static gov.ca.cwds.security.authorizer.FullyImplementedAuthorizer.callsCounts;

import com.google.inject.Inject;
import gov.ca.cwds.testapp.domain.Case;
import gov.ca.cwds.testapp.domain.CaseDTO;
import gov.ca.cwds.testapp.service.FullyImplementedAuthorizerTestService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 * CWDS Intake Team
 */
public class FullyImplementedAuthorizerTest extends AbstractApiSecurityTest {

  @Inject
  FullyImplementedAuthorizerTestService testService;

  @Test
  public void testAuthorizeById() {
    clearCallsCounts();
    testService.testAuthorizeById(2L);
    assertCallsCounts(1, 0, 0, 0);
  }

  @Test
  public void testAuthorizeByNestedId() {
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    clearCallsCounts();
    testService.testAuthorizeByNestedId(caseDTO);
    assertCallsCounts(1, 0, 0, 0);
  }

  @Test
  public void testAuthorizeByObject() {
    clearCallsCounts();
    testService.testAuthorizeByObject(new Case(2L, "valid"));
    assertCallsCounts(0, 1, 0, 0);
  }

  @Test
  public void testAuthorizeByNestedObject() {
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    clearCallsCounts();
    testService.testAuthorizeByNestedObject(caseDTO);
    assertCallsCounts(0, 1, 0, 0);
  }

  @Test
  public void testAuthorizeReturnById() {
    clearCallsCounts();
    Long id = testService.testAuthorizeReturnById();
    assertCallsCounts(1, 0, 0, 0);
    assert id == 2;
  }

  @Test
  public void testAuthorizeReturnByNestedId() {
    clearCallsCounts();
    CaseDTO caseDTO = testService.testAuthorizeReturnByNestedId();
    assertCallsCounts(1, 0, 0, 0);
    assert caseDTO != null && caseDTO.getCaseObject() != null;
    assert caseDTO.getCaseObject().getId() == 2;
  }

  @Test
  public void testAuthorizeReturnByObject() {
    clearCallsCounts();
    Case caseObject = testService.testAuthorizeReturnByObject();
    assertCallsCounts(0, 1, 0, 0);
    assert caseObject != null;
    assert caseObject.getId() == 2;
  }

  @Test
  public void testAuthorizeReturnByNullObject() {
    clearCallsCounts();
    Case caseObject = testService.testAuthorizeReturnByNullObject();
    assertCallsCounts(0, 0, 0, 0);
    assert caseObject == null;
  }

  @Test
  public void testAuthorizeReturnByNestedObject() {
    clearCallsCounts();
    CaseDTO caseDTO = testService.testAuthorizeReturnByNestedObject();
    assertCallsCounts(0, 1, 0, 0);
    assert caseDTO != null && caseDTO.getCaseObject() != null;
    assert caseDTO.getCaseObject().getId() == 2;
  }

  @Test
  public void testAuthorizeReturnByNullAndNestedObject() {
    clearCallsCounts();
    CaseDTO caseDTO = testService.testAuthorizeReturnByNullAndNestedObject();
    assertCallsCounts(0, 0, 0, 0);
    assert caseDTO == null;
  }

  @Test
  public void testReturnFiltered() {
    clearCallsCounts();
    List<Case> result = testService.testReturnFiltered();
    assertCallsCounts(0, 0, 0, 1);
    assert result.size() == 1;
    assert result.iterator().next().getName().equals("valid");
  }

  @Test
  public void testReturnFilteredByNestedId() {
    clearCallsCounts();
    Set<CaseDTO> result = testService.testReturnFilteredByNestedId();
    assertCallsCounts(0, 0, 1, 0);
    assert result.size() == 1;
    assert result.iterator().next().getCaseObject().getName().equals("valid");
  }

  @Test
  public void testReturnFilteredByNestedObject() {
    clearCallsCounts();
    List<CaseDTO> result = testService.testReturnFilteredByNestedObject();
    assertCallsCounts(0, 0, 0, 1);
    assert result.size() == 1;
    assert result.iterator().next().getCaseObject().getName().equals("valid");
  }

  @Test
  public void testFilterArgument() {
    List<Case> list = new ArrayList<>();
    list.add(new Case(1L, "invalid"));
    list.add(new Case(2L, "valid"));

    clearCallsCounts();
    List<Case> filteredList = testService.testFilterArgument(list);
    assertCallsCounts(0, 0, 0, 1);
    assert filteredList.size() == 1;
    assert filteredList.iterator().next().getName().equals("valid");
  }

  @Test
  public void testFilterArgumentByNestedId() {
    List<CaseDTO> list = new ArrayList<>();
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(1L, "invalid"));
    list.add(caseDTO);
    caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    list.add(caseDTO);

    clearCallsCounts();
    List<CaseDTO> filteredList = testService.testFilterArgumentByNestedId(list);
    assertCallsCounts(0, 0, 1, 0);
    assert filteredList.size() == 1;
    assert filteredList.iterator().next().getCaseObject().getName().equals("valid");
  }

  @Test
  public void testFilterArgumentByNestedObject() {
    Set<CaseDTO> set = new HashSet<>();
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(1L, "invalid"));
    set.add(caseDTO);
    caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    set.add(caseDTO);

    clearCallsCounts();
    Set<CaseDTO> filteredList = testService.testFilterArgumentByNestedObject(set);
    assertCallsCounts(0, 0, 0, 1);
    assert filteredList.size() == 1;
    assert filteredList.iterator().next().getCaseObject().getName().equals("valid");
  }

  private void clearCallsCounts() {
    callsCounts.clear();
  }

  private void assertCallsCounts(int expectedCheckIdCount, int expectedCheckInstanceCount,
      int expectedFilterIdsCount, int expectedFilterInstancesCount) {
    assert callsCounts.getOrDefault("checkId", 0) == expectedCheckIdCount;
    assert callsCounts.getOrDefault("checkInstance", 0) == expectedCheckInstanceCount;
    assert callsCounts.getOrDefault("filterIds", 0) == expectedFilterIdsCount;
    assert callsCounts.getOrDefault("filterInstances", 0) == expectedFilterInstancesCount;
  }
}
