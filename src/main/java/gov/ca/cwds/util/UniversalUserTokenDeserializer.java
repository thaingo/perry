package gov.ca.cwds.util;

import gov.ca.cwds.UniversalUserToken;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class UniversalUserTokenDeserializer extends JsonDeserializer<UniversalUserToken> {

  public static final String COUNTY_NAME_PARAM = "county_name";
  public static final String ADMIN_OFFICE_IDS_PARAM = "admin_office_ids";
  public static final String USER_NAME = "userName";
  public static final String FIRST_NAME = "first_name";
  public static final String LAST_NAME = "last_name";


  @Override
  public UniversalUserToken deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    Set<String> roles = parseArrayToStringSet(node.get("roles"));
    Set<String> adminOffices = parseArrayToStringSet(node.get(ADMIN_OFFICE_IDS_PARAM));
    UniversalUserToken result = new UniversalUserToken();
    result.setUserId(getParameter(node, "user"));
    result.getRoles().addAll(roles);
    result.setParameter(COUNTY_NAME_PARAM, getParameter(node, COUNTY_NAME_PARAM));
    result.setParameter(ADMIN_OFFICE_IDS_PARAM, adminOffices);
    result.setParameter(USER_NAME, getParameter(node, USER_NAME));
    result.setParameter(FIRST_NAME, getParameter(node, FIRST_NAME));
    result.setParameter(LAST_NAME, getParameter(node, LAST_NAME));
    return result;
  }

  private String getParameter(JsonNode node, String nodeName) {
    return Optional.ofNullable(node.get(nodeName)).map(JsonNode::asText).orElse(null);
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
