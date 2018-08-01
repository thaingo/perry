package gov.ca.cwds.idm.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ElasticSearchServiceTest {

  private ElasticSearchService service;

  @Before
  public void before(){
    service = new ElasticSearchService();
    service.setDoraUrl("http://localhost:8889/dora");
    service.setEsIndexName("users");
    service.setEsTypeName("user");
  }

  @Test
  public void testGetUpdateUrl(){
    assertThat(service.getUpdateUrl("128e120c-d643-44ac-ad9b-4a3fc767f04d"),
        is("http://localhost:8889/dora/users/user/128e120c-d643-44ac-ad9b-4a3fc767f04d"));
  }

  @Test
  public void testGetCreateUrl(){
    assertThat(service.getCreateUrl("128e120c-d643-44ac-ad9b-4a3fc767f04d"),
        is("http://localhost:8889/dora/users/user/128e120c-d643-44ac-ad9b-4a3fc767f04d/_create"));
  }
}
