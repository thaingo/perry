package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.model.OperationType;
import gov.ca.cwds.idm.service.cognito.ElasticSearchProperties;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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

  private static final String CREATE_URL_TEMPLATE =
      "{" + DORA_URL + "}/{" + ES_USER_INDEX + "}/{" + ES_USER_TYPE + "}/{" + ID + "}/_create";

  private static final String UPDATE_URL_TEMPLATE =
      "{" + DORA_URL + "}/{" + ES_USER_INDEX + "}/{" + ES_USER_TYPE + "}/{" + ID + "}";

  @Autowired private ElasticSearchProperties esProperties;

  @Autowired
  private RestTemplate restTemplate;

  public void createUser(User user) {
    putUser(user, OperationType.CREATE);
    LOGGER.info("User, username:{} was successfully inserted in Elastic Search index", user.getId());
  }

  public void updateUser(User user) {
    putUser(user, OperationType.UPDATE);
    LOGGER.info("User, username:{} was successfully updated in Elastic Search index", user.getId());
  }

  private void putUser(User user, OperationType operation) {

    String urlTemplate = getUrlTemplate(operation);

    String id = user.getId();
    id = "128e120c-d643-44ac-ad9b-4a3fc767f04d";//for mock

    Map<String, String> params = new HashMap<>();
    params.put(DORA_URL, esProperties.getDoraUrl());
    params.put(ES_USER_INDEX, esProperties.getIndex());
    params.put(ES_USER_TYPE, esProperties.getType());
    params.put(ID, id);

    restTemplate.put(urlTemplate, user, params);
  }

  private String getUrlTemplate(OperationType operation){
    if(operation == OperationType.CREATE) {
      return CREATE_URL_TEMPLATE;
    } else if(operation == OperationType.UPDATE){
      return UPDATE_URL_TEMPLATE;
    } else {
      throw new IllegalArgumentException("Provided unsupported OperationType: " + operation.toString());
    }
  }

  public void setElasticSearchProperties(ElasticSearchProperties esProperties) {
    this.esProperties = esProperties;
  }
}
