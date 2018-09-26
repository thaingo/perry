package gov.ca.cwds.testapp.service;

import gov.ca.cwds.testapp.domain.Case;
import gov.ca.cwds.testapp.domain.CaseDTO;
import java.util.List;
import java.util.Set;

/**
 * CWDS Intake Team
 */
public interface FullyImplementedAuthorizerTestService {

  void testAuthorizeById(Long id);

  void testAuthorizeByNestedId(CaseDTO caseDTO);

  void testAuthorizeByObject(Case caseObject);

  void testAuthorizeByNestedObject(CaseDTO caseDTO);

  Long testAuthorizeReturnById();

  CaseDTO testAuthorizeReturnByNestedId();

  Case testAuthorizeReturnByObject();

  Case testAuthorizeReturnByNullObject();

  CaseDTO testAuthorizeReturnByNestedObject();

  CaseDTO testAuthorizeReturnByNullAndNestedObject();

  List<Case> testReturnFiltered();

  Set<CaseDTO> testReturnFilteredByNestedId();

  List<CaseDTO> testReturnFilteredByNestedObject();

  List<Case> testFilterArgument(List<Case> caseList);

  List<CaseDTO> testFilterArgumentByNestedId(List<CaseDTO> caseDTOList);

  Set<CaseDTO> testFilterArgumentByNestedObject(Set<CaseDTO> caseDTOSet);
}
