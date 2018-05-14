package gov.ca.cwds.idm.util;

import static io.dropwizard.testing.FixtureHelpers.fixture;

import java.io.IOException;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

public final class AssertFixtureUtils {

  private AssertFixtureUtils() {
  }

  public static void assertStrict(MvcResult result, String fixturePath) throws IOException, JSONException {
    MockHttpServletResponse response = result.getResponse();
    String strResponse = response.getContentAsString();
    JSONAssert.assertEquals(fixture(fixturePath), strResponse, true);
  }

}
