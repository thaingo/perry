package gov.ca.cwds.security;

import com.google.inject.Inject;
import gov.ca.cwds.testapp.domain.Case;
import gov.ca.cwds.testapp.domain.CaseDTO;
import gov.ca.cwds.testapp.service.TestService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Test;

/**
 * Created by dmitry.rudenko on 10/6/2017.
 */
public class ApiSecurityTest extends AbstractApiSecurityTest {

  @Inject
  TestService testService;

  @Test(expected = UnauthorizedException.class)
  public void testUnauthorized() {
    testService.testArg("2");
  }

  @Test
  public void testAuthorized() {
    testService.testArg("1");
  }

  @Test
  public void testStaticAuthorized()  {
    testService.getById(1L);
  }

  @Test(expected = UnauthorizedException.class)
  public void testStaticUnauthorized()  {
    testService.updateCase(1L, "name");
  }

  @Test
  public void testArgAuthorizedCompositeObject() {
    CaseDTO caseDTO = new CaseDTO();
    Case caseObject = new Case(1L, "name");
    caseDTO.setCaseObject(caseObject);
    testService.testCompositeObject(caseDTO);
  }

  @Test
  public void testArgAuthorizedNullCompositeObject() {
    testService.testCompositeObject(null);
  }

  @Test(expected = UnauthorizedException.class)
  public void testArgUnauthorizedCompositeObject() {
    CaseDTO caseDTO = new CaseDTO();
    Case caseObject = new Case(2L, "name");
    caseDTO.setCaseObject(caseObject);
    testService.testCompositeObject(caseDTO);
  }

  @Test
  public void testArgAuthorizedCompositeObjectList() {
    CaseDTO caseDTO = new CaseDTO();
    Case caseObject = new Case(1L, "name");
    caseDTO.getCases().add(caseObject);
    caseDTO.getCases().add(new Case(1L, "name"));
    testService.testCompositeObjectList(caseDTO);
  }

  @Test(expected = UnauthorizedException.class)
  public void testArgUnauthorizedCompositeObjectList() {
    CaseDTO caseDTO = new CaseDTO();
    Case caseObject = new Case(1L, "name");
    caseDTO.getCases().add(caseObject);
    caseDTO.getCases().add(new Case(2L, "name"));
    testService.testCompositeObjectList(caseDTO);
  }

  @Test
  public void testRetAuthorizedCompositeObject() {
    CaseDTO caseDTO = testService.testReturnInstance();
    assert  caseDTO != null;
  }

  @Test(expected = UnauthorizedException.class)
  public void testRetUnauthorizedCompositeObject() {
    testService.testReturnProtectedInstance();
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
    list.add(new Case(1L, "valid"));
    list.add(new Case(2L, "name"));

    List<Case> filteredList = testService.testFilterArgument(list);
    assert filteredList.size() == 1;
    assert filteredList.iterator().next().getName().equals("valid");
  }

  @Test
  public void testFilterArgumentByNestedId() {
    List<CaseDTO> list = new ArrayList<>();
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(1L, "valid"));
    list.add(caseDTO);
    caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "name"));
    list.add(caseDTO);

    List<CaseDTO> filteredList = testService.testFilterArgumentByNestedId(list);
    assert filteredList.size() == 1;
    assert filteredList.iterator().next().getCaseObject().getName().equals("valid");
  }

  @Test
  public void testFilterArgumentByNestedObject() {
    Set<CaseDTO> set = new HashSet<>();
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(1L, "valid"));
    set.add(caseDTO);
    caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "name"));
    set.add(caseDTO);

    Set<CaseDTO> filteredList = testService.testFilterArgumentByNestedObject(set);
    assert filteredList.size() == 1;
    assert filteredList.iterator().next().getCaseObject().getName().equals("valid");
  }

  @Test
  public void testDirectCheck() {
    SecurityUtils.getSubject().checkPermission("case:read:1");

  }

  @Test(expected = UnauthorizedException.class)
  public void testDirectCheckUnauthorized() {
    SecurityUtils.getSubject().checkPermission("case:read:2");
  }
}
