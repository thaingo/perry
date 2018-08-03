package gov.ca.cwds.idm.service;

import static gov.ca.cwds.util.CurrentAuthenticatedUserUtil.getSsoToken;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.model.OperationType;
import gov.ca.cwds.idm.service.cognito.ElasticSearchProperties;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("idm")
public class ElasticSearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchService.class);

  private static final String DORA_URL = "doraUrl";
  private static final String ES_USER_INDEX = "esUserIndex";
  private static final String ES_USER_TYPE = "esUserType";
  private static final String ID = "id";
  private static final String SSO_TOKEN = "ssoToken";

  private static final String CREATE_URL_TEMPLATE =
      "{"
          + DORA_URL
          + "}/{"
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
          + "}/{"
          + ES_USER_INDEX
          + "}/{"
          + ES_USER_TYPE
          + "}/{"
          + ID
          + "}?token={"
          + SSO_TOKEN
          + "}";

  @Autowired private ElasticSearchProperties esProperties;

  @Autowired private RestTemplate restTemplate;

  public ResponseEntity<String> createUser(User user) {
    return putUser(user, OperationType.CREATE);
  }

  public ResponseEntity<String> updateUser(User user) {
    return putUser(user, OperationType.UPDATE);
  }

  private ResponseEntity<String> putUser(User user, OperationType operation) {

    if (operation == null) {
      throw new IllegalArgumentException("User operation type is null");
    }

    HttpEntity<User> requestUpdate = new HttpEntity<>(user);
    String urlTemplate = getUrlTemplate(operation);
    String id = user.getId();
//    id = "128e120c-d643-44ac-ad9b-4a3fc767f04d"; // for SoapUI mock

    Map<String, String> params = new HashMap<>();
    params.put(DORA_URL, esProperties.getDoraUrl());
    params.put(ES_USER_INDEX, esProperties.getIndex());
    params.put(ES_USER_TYPE, esProperties.getType());
    params.put(ID, id);
    params.put(SSO_TOKEN, getSsoToken());

    ResponseEntity<String> response =
        restTemplate.exchange(urlTemplate, HttpMethod.PUT, requestUpdate, String.class, params);
    LOGGER.info(
        "User, username:{} was successfully {}d in Elastic Search index, Dora response string is:{}",
        user.getId(),
        operation.toString().toLowerCase(),
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

  public void setElasticSearchProperties(ElasticSearchProperties esProperties) {
    this.esProperties = esProperties;
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
}
