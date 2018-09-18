package gov.ca.cwds.util;

import gov.ca.cwds.UniversalUserToken;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

public class UniversalUserTokenDeserializer extends JsonDeserializer<UniversalUserToken> {

  public static final String COUNTY_NAME_PARAM = "county_name";
  public static final String ADMIN_OFFICE_IDS_PARAM = "admin_office_ids";

  @Override
  public UniversalUserToken deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    String userId = Optional.ofNullable(node.get("user")).map(JsonNode::asText).orElse(null);
    String countyName =
        Optional.ofNullable(node.get(COUNTY_NAME_PARAM)).map(JsonNode::asText).orElse(null);
    Set<String> roles = parseArrayToStringSet(node.get("roles"));
    Set<String> adminOffices = parseArrayToStringSet(node.get(ADMIN_OFFICE_IDS_PARAM));
    UniversalUserToken result = new UniversalUserToken();
    result.setUserId(userId);
    result.getRoles().addAll(roles);
    result.setParameter(COUNTY_NAME_PARAM, countyName);
    result.setParameter(ADMIN_OFFICE_IDS_PARAM, adminOffices);
    return result;
  }

  private Set<String> parseArrayToStringSet(JsonNode node) {
    Set<String> result = new HashSet<>();
    if (node != null && node.isArray()) {
      StreamSupport.stream(node.spliterator(), false)
          .forEach(r -> result.add(r.asText()));
    }
    return result;
  }
}
