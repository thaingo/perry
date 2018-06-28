package gov.ca.cwds.testapp.service;

import gov.ca.cwds.security.annotations.Authorize;
import gov.ca.cwds.testapp.domain.Case;
import gov.ca.cwds.testapp.domain.CaseDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CWDS Intake Team
 */
public class FilterOnlyAuthorizerTestServiceImpl implements FilterOnlyAuthorizerTestService {

  @Override
  public void testAuthorizeById(@Authorize("case:filter:id") Long id) {
    System.out.println();
  }

  @Override
  public void testAuthorizeByNestedId(@Authorize("case:filter:caseDTO.caseObject.id") CaseDTO caseDTO) {
    System.out.println();
  }

  @Override
  public void testAuthorizeByObject(@Authorize("case:filter:caseObject") Case caseObject) {
    System.out.println();
  }

  @Override
  public void testAuthorizeByNestedObject(@Authorize("case:filter:caseDTO.caseObject") CaseDTO caseDTO) {
    System.out.println();
  }

  @Override
  @Authorize("case:filter:id")
  public Long testAuthorizeReturnById() {
    return 2L;
  }

  @Override
  @Authorize("case:filter:caseDTO.caseObject.id")
  public CaseDTO testAuthorizeReturnByNestedId() {
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    return caseDTO;
  }

  @Override
  @Authorize("case:filter:caseObject")
  public Case testAuthorizeReturnByObject() {
    return new Case(2L, "valid");
  }

  @Override
  @Authorize("case:filter:caseDTO.caseObject")
  public CaseDTO testAuthorizeReturnByNestedObject() {
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    return caseDTO;
  }

  @Override
  @Authorize("case:filter:cases")
  public List<Case> testReturnFiltered() {
    List<Case> result = new ArrayList<>();
    result.add(new Case(1L, "invalid"));
    result.add(new Case(2L, "valid"));
    return result;
  }

  @Override
  @Authorize("case:filter:caseDTO.caseObject.id")
  public Set<CaseDTO> testReturnFilteredByNestedId() {
    Set<CaseDTO> result = new HashSet<>();
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(1L, "invalid"));
    result.add(caseDTO);
    caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    result.add(caseDTO);
    return result;
  }

  @Override
  @Authorize("case:filter:caseDTO.caseObject")
  public List<CaseDTO> testReturnFilteredByNestedObject() {
    List<CaseDTO> result = new ArrayList<>();
    CaseDTO caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(1L, "invalid"));
    result.add(caseDTO);
    caseDTO = new CaseDTO();
    caseDTO.setCaseObject(new Case(2L, "valid"));
    result.add(caseDTO);
    return result;
  }

  @Override
  public List<Case> testFilterArgument(@Authorize("case:filter:caseList") List<Case> caseList) {
    return caseList;
  }

  @Override
  public List<CaseDTO> testFilterArgumentByNestedId(@Authorize("case:filter:caseDTO.caseObject.id") List<CaseDTO> caseDTOList) {
    return caseDTOList;
  }

  @Override
  public Set<CaseDTO> testFilterArgumentByNestedObject(@Authorize("case:filter:caseDTO.caseObject") Set<CaseDTO> caseDTOSet) {
    return caseDTOSet;
  }
}
