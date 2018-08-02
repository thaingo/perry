package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.persistence.model.OperationType;
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

  private static final String CREATE_URL_TEMPLATE ="{doraUrl}/{esIndexName}/{esTypeName}/{id}/_create";
  private static final String UPDATE_URL_TEMPLATE ="{doraUrl}/{esIndexName}/{esTypeName}/{id}";

  @Autowired
  private RestTemplate restTemplate;

  private String doraUrl = "http://localhost:8889/dora";
  private String esIndexName = "users";
  private String esTypeName = "user";

  public void createUser(User user) {
    putUser(user, OperationType.CREATE);
    LOGGER.info("User, username:{} was successfully inserted in Elastic Search index", user.getId());
  }

  public void updateUser(User user) {
    putUser(user, OperationType.UPDATE);
    LOGGER.info("User, username:{} was successfully updated in Elastic Search index", user.getId());
  }

  private void putUser(User user, OperationType operation) {

    String urlTemplate;
    if(operation == OperationType.CREATE) {
      urlTemplate = CREATE_URL_TEMPLATE;
    } else if(operation == OperationType.UPDATE){
      urlTemplate = UPDATE_URL_TEMPLATE;
    } else {
      throw new IllegalArgumentException("Provided unsapported OperationType: " + operation.toString());
    }

    String id = user.getId();
    id = "128e120c-d643-44ac-ad9b-4a3fc767f04d";//for mock

    Map<String, String> params = new HashMap<>();
    params.put("doraUrl", doraUrl);
    params.put("esIndexName", esIndexName);
    params.put("esTypeName", esTypeName);
    params.put("id", id);

    restTemplate.put(urlTemplate, user, params);
  }

  public void setDoraUrl(String doraUrl) {
    this.doraUrl = doraUrl;
  }

  public void setEsIndexName(String esIndexName) {
    this.esIndexName = esIndexName;
  }

  public void setEsTypeName(String esTypeName) {
    this.esTypeName = esTypeName;
  }
}
