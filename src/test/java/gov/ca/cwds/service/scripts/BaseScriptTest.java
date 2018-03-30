package gov.ca.cwds.service.scripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.UniversalUserToken;
import gov.ca.cwds.rest.api.domain.auth.StaffAuthorityPrivilege;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import io.dropwizard.jackson.Jackson;
import org.junit.Assert;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.dropwizard.testing.FixtureHelpers.fixture;

/**
 * Created by dmitry.rudenko on 8/16/2017.
 */
public class BaseScriptTest {
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
  public void test(String script, String json, String userAuthorization) throws Exception {
    IdentityMappingScript identityMappingScript = loadScript(script);
    UniversalUserToken user = createUniversalUserToken();
    UserAuthorization authorization = MAPPER.readValue(
            fixture(userAuthorization),
            UserAuthorization.class);
    user.setAuthorization(authorization);
    String result = identityMappingScript.map(user);
    System.out.println(result);
    String expectedResult = readResource(json);
    Assert.assertEquals(expectedResult, result);

  }

  protected UniversalUserToken createUniversalUserToken() {
    return new UniversalUserToken();
  }

  private IdentityMappingScript loadScript(String script) throws Exception {
    return new IdentityMappingScript(getPath(script).toString());
  }

  private String readResource(String resource) throws Exception {
    return new String(Files.readAllBytes(getPath(resource)));
  }

  private Path getPath(String resource) throws Exception {
    return Paths.get(getClass().getResource(resource).toURI());
  }
}
