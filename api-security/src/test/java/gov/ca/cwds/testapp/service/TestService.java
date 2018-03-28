package gov.ca.cwds.testapp.service;

import gov.ca.cwds.testapp.domain.Case;
import gov.ca.cwds.testapp.domain.CaseDTO;

import java.util.List;

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

  List<CaseDTO> testFilter() ;
}
