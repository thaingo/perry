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

  public void createUser(User user) {

  }

  public void updateUser(User user) {

  }
}
