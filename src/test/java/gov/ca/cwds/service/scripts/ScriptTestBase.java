package gov.ca.cwds.service.scripts;

import static io.dropwizard.testing.FixtureHelpers.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import io.dropwizard.jackson.Jackson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * Created by dmitry.rudenko on 8/16/2017.
 */
public class ScriptTestBase {
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
  public void test(String script, String json, String userAuthorization) throws Exception {
    IdentityMappingScript identityMappingScript = loadScript(script);
    UniversalUserToken user = createUniversalUserToken();
    if(userAuthorization != null) {
      UserAuthorization authorization = MAPPER.readValue(
          fixture(userAuthorization),
          UserAuthorization.class);
      user.setAuthorization(authorization);
    }
    String result = identityMappingScript.map(user);
    System.out.println(result);
    String expectedResult = readResource(json);
    JSONAssert.assertEquals(expectedResult, result, JSONCompareMode.STRICT);
  }

  protected UniversalUserToken createUniversalUserToken() {
    return new UniversalUserToken();
  }

  private IdentityMappingScript loadScript(String script) throws Exception {
    return new IdentityMappingScript(getPath(script).toString());
  }

  public static String readResource(String resource) throws Exception {
    return new String(Files.readAllBytes(getPath(resource)));
  }

  private static Path getPath(String resource) throws Exception {
    return Paths.get(ScriptTestBase.class.getResource(resource).toURI());
  }
}
