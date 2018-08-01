package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("idm")
public class ElasticSearchService {

  @Autowired
  private RestTemplate restTemplate;

  private static final String ES_CREATE_ENDPOINT = "/_create";

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchService.class);

  private String doraUrl = "http://localhost:8889/dora";
  private String esIndexName = "users";
  private String esTypeName = "user";

  public void createUser(User user) {
    String id = user.getId();
    id = "128e120c-d643-44ac-ad9b-4a3fc767f04d";//for mock
    String url = getCreateUrl(id);
    restTemplate.put(url, user);
    LOGGER.info("User, username:{} was successfully inserted in Elastic Search index", id);
  }

  public void updateUser(User user) {
    String id = user.getId();
    id = "128e120c-d643-44ac-ad9b-4a3fc767f04d";//for mock
    String url = getUpdateUrl(id);
    restTemplate.put(url, user);
    LOGGER.info("User, username:{} was successfully updated in Elastic Search index", id);
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

  String getUpdateUrl(String id) {
    return doraUrl + '/' + esIndexName + '/' + esTypeName + '/' + id;
  }

  String getCreateUrl(String id) {
    return getUpdateUrl(id) + ES_CREATE_ENDPOINT;
  }
}
