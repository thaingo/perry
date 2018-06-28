package gov.ca.cwds.security;

import com.google.inject.Inject;
import gov.ca.cwds.testapp.domain.Case;
import gov.ca.cwds.testapp.domain.CaseDTO;
import gov.ca.cwds.testapp.service.FilterOnlyAuthorizerTestService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.shiro.authz.AuthorizationException;
import org.junit.Test;

/**
 * CWDS Intake Team
 */
public class FilterOnlyAuthorizerTest extends AbstractApiSecurityTest {

  @Inject
  FilterOnlyAuthorizerTestService testService;

  @Test(expected = AuthorizationException.class)
  public void testAuthorizeById() {
    testService.testAuthorizeById(2L);
  }

  @Test(expected = AuthorizationException.class)
  public void testAuthorizeByNestedId() {
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    testService.testAuthorizeByNestedId(caseDTO);
  }

  @Test(expected = AuthorizationException.class)
  public void testAuthorizeByObject() {
    testService.testAuthorizeByObject(new Case(2L, "valid"));
  }

  @Test(expected = AuthorizationException.class)
  public void testAuthorizeByNestedObject() {
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    testService.testAuthorizeByNestedObject(caseDTO);
  }

  @Test(expected = AuthorizationException.class)
  public void testAuthorizeReturnById() {
    testService.testAuthorizeReturnById();
  }

  @Test(expected = AuthorizationException.class)
  public void testAuthorizeReturnByNestedId() {
    testService.testAuthorizeReturnByNestedId();
  }

  @Test(expected = AuthorizationException.class)
  public void testAuthorizeReturnByObject() {
    testService.testAuthorizeReturnByObject();
  }

  @Test(expected = AuthorizationException.class)
  public void testAuthorizeReturnByNestedObject() {
    testService.testAuthorizeReturnByNestedObject();
  }

  @Test
  public void testReturnFiltered() {
    List<Case> result = testService.testReturnFiltered();
    assert result.size() == 1;
    assert result.iterator().next().getName().equals("valid");
  }

  @Test
  public void testReturnFilteredByNestedId() {
    Set<CaseDTO> result = testService.testReturnFilteredByNestedId();
    assert result.size() == 1;
    assert result.iterator().next().getCaseObject().getName().equals("valid");
  }

  @Test
  public void testReturnFilteredByNestedObject() {
    List<CaseDTO> result = testService.testReturnFilteredByNestedObject();
    assert result.size() == 1;
    assert result.iterator().next().getCaseObject().getName().equals("valid");
  }

  @Test
  public void testFilterArgument() {
    List<Case> list = new ArrayList<>();
    list.add(new Case(1L, "invalid"));
    list.add(new Case(2L, "valid"));

    List<Case> filteredList = testService.testFilterArgument(list);
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

    List<CaseDTO> filteredList = testService.testFilterArgumentByNestedId(list);
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

    Set<CaseDTO> filteredList = testService.testFilterArgumentByNestedObject(set);
    assert filteredList.size() == 1;
    assert filteredList.iterator().next().getCaseObject().getName().equals("valid");
  }
}
