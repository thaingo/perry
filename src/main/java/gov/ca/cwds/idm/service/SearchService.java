package gov.ca.cwds.idm.service;

import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getSsoToken;
import static gov.ca.cwds.util.Utils.toLowerCase;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.model.OperationType;
import gov.ca.cwds.idm.service.cognito.SearchProperties;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("idm")
public class SearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

  private static final String DORA_URL = "doraUrl";
  private static final String ES_USER_INDEX = "esUserIndex";
  private static final String ES_USER_TYPE = "esUserType";
  private static final String ID = "id";
  private static final String SSO_TOKEN = "ssoToken";

  private static final String CREATE_URL_TEMPLATE =
      "{"
          + DORA_URL
          + "}/dora/{"
          + ES_USER_INDEX
          + "}/{"
          + ES_USER_TYPE
          + "}/{"
          + ID
          + "}/_create?token={"
          + SSO_TOKEN
          + "}";

  private static final String UPDATE_URL_TEMPLATE =
      "{"
          + DORA_URL
          + "}/dora/{"
          + ES_USER_INDEX
          + "}/{"
          + ES_USER_TYPE
          + "}/{"
          + ID
          + "}?token={"
          + SSO_TOKEN
          + "}";

  @Autowired private SearchProperties searchProperties;

  @Autowired private RestTemplate restTemplate;

  public ResponseEntity<String> createUser(User user) {
    return putUser(user, OperationType.CREATE);
  }

  public ResponseEntity<String> updateUser(User user) {
    return putUser(user, OperationType.UPDATE);
  }

  ResponseEntity<String> putUser(User user, OperationType operation) {

    if (operation == null) {
      throw new IllegalArgumentException("User operation type is null");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);

    String urlTemplate = getUrlTemplate(operation);

    Map<String, String> params = new HashMap<>();
    params.put(DORA_URL, searchProperties.getDoraUrl());
    params.put(ES_USER_INDEX, searchProperties.getIndex());
    params.put(ES_USER_TYPE, searchProperties.getType());
    params.put(ID, user.getId());
    params.put(SSO_TOKEN, getSsoToken());

    ResponseEntity<String> response =
        restTemplate.exchange(urlTemplate, HttpMethod.PUT, requestEntity, String.class, params);
    LOGGER.info(
        "User, username:{} was successfully {}d in Elastic Search index, Dora response string is:{}",
        user.getId(),
        toLowerCase(operation.toString()),
        response.getBody());
    return response;
  }

  static String getUrlTemplate(OperationType operation) {
    if (operation == OperationType.CREATE) {
      return CREATE_URL_TEMPLATE;
    } else if (operation == OperationType.UPDATE) {
      return UPDATE_URL_TEMPLATE;
    } else {
      throw new IllegalArgumentException(
          "Provided unsupported OperationType: " + operation.toString());
    }
  }

  public void setSearchProperties(SearchProperties searchProperties) {
    this.searchProperties = searchProperties;
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
}
