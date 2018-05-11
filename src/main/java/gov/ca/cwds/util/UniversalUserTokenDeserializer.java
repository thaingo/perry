package gov.ca.cwds.util;

import gov.ca.cwds.UniversalUserToken;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UniversalUserTokenDeserializer extends JsonDeserializer<UniversalUserToken> {

  @Override
  public UniversalUserToken deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    JsonNode node = jp.getCodec().readTree(jp);
    String userId = node.get("user").asText();
    String countyName = node.get("county_name").asText();
    Set<String> roles = new HashSet<>();
    for (JsonNode child : node.get("roles")) {
      roles.add(child.asText());
    }
    UniversalUserToken result = new UniversalUserToken();
    result.setUserId(userId);
    result.getRoles().addAll(roles);
    result.setParameter("county_name", countyName);
    return result;
  }
}
