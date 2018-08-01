package gov.ca.cwds.idm.service;

import gov.ca.cwds.idm.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("idm")
public class ElasticSearchService {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private UserLogService userLogService;

  private static final String ES_CREATE_ENDPOINT = "/_create";

  private String doraUrl = "http://localhost:8889/dora";
  private String esIndexName = "users";
  private String esTypeName = "user";


  public void createUser(User user) {

  }

  public void updateUser(User user) {

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
