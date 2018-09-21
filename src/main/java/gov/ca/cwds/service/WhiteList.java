package gov.ca.cwds.service;

import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.rest.api.domain.PerryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import org.springframework.util.AntPathMatcher;

/**
 * Created by dmitry.rudenko on 9/18/2017.
 */
@Component
public class WhiteList {

  PerryProperties configuration;

  private AntPathMatcher pathMatcher = new AntPathMatcher();

  public void validate(String param, String url) {
    List<String> patterns = configuration.getWhiteList();
    if (!disabled(patterns) && !(anyMatch(url, patterns)
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

  private boolean anyMatch(String url, List<String> patterns) {
    return patterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, url));
  }

}
