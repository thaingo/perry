package gov.ca.cwds.service;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.rest.api.domain.PerryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by dmitry.rudenko on 9/18/2017.
 */
@Component
public class WhiteList {

  PerryProperties configuration;

  public void validate(String param, String url) {
    if (!disabled(configuration.getWhiteList())
        && !(configuration.getWhiteList().contains(url)
        || configuration.getHomePageUrl().equals(url))) {
      throw new PerryException(param + ": " + url + " is not registered");
    }
  }

  @Autowired
  public void setConfiguration(PerryProperties configuration) {
    this.configuration = configuration;
  }

  private boolean disabled(List<String> whiteList) {
    return whiteList.size() == 1 && whiteList.contains("*");
  }

}
