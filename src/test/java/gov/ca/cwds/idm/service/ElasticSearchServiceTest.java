package gov.ca.cwds.idm.service;

import static gov.ca.cwds.idm.service.ElasticSearchService.getUrlTemplate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.idm.persistence.model.OperationType;
import org.junit.Test;

public class ElasticSearchServiceTest {

  @Test
  public void testGetUrlTemplate(){
    assertThat(getUrlTemplate(OperationType.CREATE),
        is("{doraUrl}/{esUserIndex}/{esUserType}/{id}/_create"));
    assertThat(getUrlTemplate(OperationType.UPDATE),
        is("{doraUrl}/{esUserIndex}/{esUserType}/{id}"));
  }
}
