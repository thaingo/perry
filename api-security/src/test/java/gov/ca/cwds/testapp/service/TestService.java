package gov.ca.cwds.testapp.service;

import gov.ca.cwds.testapp.domain.Case;
import gov.ca.cwds.testapp.domain.CaseDTO;

import java.util.List;
import java.util.Set;

/**
 * Created by dmitry.rudenko on 10/6/2017.
 */
public interface TestService {
  void testArg(String id);

  void testCompositeObject(CaseDTO caseDTO);

  void testCompositeObjectList(CaseDTO caseDTO);

  CaseDTO testReturnInstance();

  CaseDTO testReturnProtectedInstance() ;

  Case getById(Long id);

  void updateCase(Long id, String name);

  List<Case> testReturnFiltered();

  Set<CaseDTO> testReturnFilteredByNestedId();

  List<CaseDTO> testReturnFilteredByNestedObject();

  List<Case> testFilterArgument(List<Case> caseList);

  List<CaseDTO> testFilterArgumentByNestedId(List<CaseDTO> caseDTOList);

  Set<CaseDTO> testFilterArgumentByNestedObject(Set<CaseDTO> caseDTOSet);

  List<Case> testBatchReturnFiltered();

  Set<CaseDTO> testBatchReturnFilteredByNestedId();

  List<CaseDTO> testBatchReturnFilteredByNestedObject();

  List<Case> testBatchFilterArgument(List<Case> caseList);

  List<CaseDTO> testBatchFilterArgumentByNestedId(List<CaseDTO> caseDTOList);

  Set<CaseDTO> testBatchFilterArgumentByNestedObject(Set<CaseDTO> caseDTOSet);
}
