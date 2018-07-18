package gov.ca.cwds.web;

import gov.ca.cwds.data.reissue.model.PerryTokenEntity;
import gov.ca.cwds.service.sso.OAuth2Service;
import io.dropwizard.testing.FixtureHelpers;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Profile("test")
public class MockOAuth2Service extends OAuth2Service {


  public static final String EXPECTED_SSO_TOKEN = "eyJraWQiOiJzWUFcL1VUTGdSTis4cTJSRUxEZXdBamhGd0RWaVR2Tm1DYThlMzYrMUZwOD0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI5YTExNTg1ZC0wYTg2LTQ3MTUtYmVkZi0zY2Y3ODNiYzRiYWYiLCJkZXZpY2Vfa2V5IjoidXMtd2VzdC0yX2UyN2E2Y2IxLWVjYTQtNDQxYy1iMmQxLTRmZDZkNWExOWYwYiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE1MzA4OTkwNzMsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy13ZXN0LTIuYW1hem9uYXdzLmNvbVwvdXMtd2VzdC0yX2JVdEFTeFV6NiIsImV4cCI6MTUzMDkwMjY3MywiaWF0IjoxNTMwODk5MDczLCJqdGkiOiIwNDBlNTg4MS0yMzY5LTQ2MmEtOTYzNS00MmQ0ZDI2YThkODYiLCJjbGllbnRfaWQiOiIyYTFkZjF2OGJyNjBpNTJxb2ZpNHFta2oyayIsInVzZXJuYW1lIjoiOWExMTU4NWQtMGE4Ni00NzE1LWJlZGYtM2NmNzgzYmM0YmFmIn0.MTvqzH5DtLlNkJIv6fP0DbL62jW2Dv6Yca1l-XBVQ8gLEwtZyrva5rqtH1W-wXSzmzCkJ8WRm3JjrvayJZa5yjy4HjIfPumh4mfQOm5XFO3RR8GGBjPv5wl6aCzcZAX6Stxk88XoqlrasneqzAMo1L9xeTrKQe4UZ8ame-RiHfGAy-if2C4dNMbrX9SfYqqf0DDyW-dG83ijQJ-dzL4Hjim0YYXJ1jV43oRvv4R6dz3RnyYu9KxOTUjf9QLuIjuoKQxil0mhPavOyCMARin2kfzdGMg2Tp5ETJWW0LyetLGYvOHSUOJ2Lp2njZH1H92z5IjI4UQu5zC11tFoheN5DQ";

  @Override
  public Map getUserInfo(String ssoToken) {
    if (EXPECTED_SSO_TOKEN
        .equals(ssoToken)) {
      return constructUserInfo("fixtures/mfa/mfa-response.json");
    } else {
      throw new RuntimeException("Unexpected SSO token in getUserInformation method");
    }
  }

  @Override
  @Retryable(interceptor = "retryInterceptor", value = HttpClientErrorException.class)
  public void validate(PerryTokenEntity perryTokenEntity) {
    if (EXPECTED_SSO_TOKEN.equals(perryTokenEntity.getSsoToken())) {
      return;
    } else {
      throw new RuntimeException("Invalid PerryTokenEntity in validate method");
    }
  }

  @Override
  public void invalidate(String ssoToken) {
    System.out.println("Invalidating sso token: " + ssoToken);
  }

  public static Map<String, Object> constructUserInfo(String mfaJson) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode cognitoJson = objectMapper
          .readValue(FixtureHelpers.fixture(mfaJson), JsonNode.class);
      JsonNode payloadNode = cognitoJson.get("idToken").get("payload");
      Map payloadMap = objectMapper.convertValue(payloadNode, Map.class);
      Map<String, Object> userInfo = new HashMap<>();
      userInfo.put("UserAttributes", mapToNameValueList(payloadMap));
      userInfo.put("Username", payloadMap.get("cognito:username"));
      return userInfo;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private static List<Map> mapToNameValueList(Map input) {
    List<Map> result = new LinkedList<>();
    input.forEach((key, value) -> {
      Map nameValue = new HashMap<>();
      nameValue.put("Name", String.valueOf(key));
      nameValue.put("Value", String.valueOf(value));
      result.add(nameValue);
    });
    return result;
  }

}
