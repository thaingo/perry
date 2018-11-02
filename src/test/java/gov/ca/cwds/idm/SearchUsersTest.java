package gov.ca.cwds.idm;

import static gov.ca.cwds.idm.util.AssertFixtureUtils.assertNonStrict;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class SearchUsersTest extends BaseIdmResourceTest {

  @Test
  public void testSearchUsersByRacfid() throws Exception {

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/idm/users/search")
                    .contentType(JSON_CONTENT_TYPE)
                    .content("[\"YOLOD\", \"SMITHBO\"]")
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/users-search/valid.json");
  }

  @Test
  public void testSearchUsersByRacfidFilterOutRepeats() throws Exception {

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/idm/users/search")
                    .contentType(JSON_CONTENT_TYPE)
                    .content("[\"YOLOD\", \"yolod\", \"YOLOD\"]")
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(JSON_CONTENT_TYPE))
            .andReturn();

    assertNonStrict(result, "fixtures/idm/users-search/yolod.json");
  }
}
