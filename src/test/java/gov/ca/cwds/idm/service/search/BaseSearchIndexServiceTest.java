package gov.ca.cwds.idm.service.search;

import static gov.ca.cwds.idm.service.search.BaseSearchIndexService.getUrlTemplate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.idm.persistence.ns.OperationType;
import org.junit.Test;

public class BaseSearchIndexServiceTest {
  @Test
  public void testGetUrlTemplate() {
    assertThat(
        getUrlTemplate(OperationType.CREATE),
        is("{doraUrl}/dora/{esIndexName}/{esIndexType}/{id}/_create?token={ssoToken}"));
    assertThat(
        getUrlTemplate(OperationType.UPDATE),
        is("{doraUrl}/dora/{esIndexName}/{esIndexType}/{id}?token={ssoToken}"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetUrlTemplateNull() {
    getUrlTemplate(null);
  }
}
