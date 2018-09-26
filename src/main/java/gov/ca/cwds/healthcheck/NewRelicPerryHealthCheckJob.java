package gov.ca.cwds.healthcheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.newrelic.api.agent.NewRelic;

@Component
public class NewRelicPerryHealthCheckJob {

  private static final String HEALTHY_ATTRIBUTE = "healthy";
  private static final String UNHEALTHY_ATTRIBUTE = "unhealthy";
  private static final String HEALTH_CHECK_EVENT = "PerryHealthCheck";


  @Autowired
  private HealthEndpoint healthEndpoint;

  @Scheduled(cron = "0 0/${newrelic.fixed.delay.minutes:10} * * * ?")
  public void reportPerryHealthCheckToNewRelic() {

    Map<String, Object> eventAttributes = new TreeMap<>();

    List<String> healthy = new ArrayList<>();
    List<String> unhealthy = new ArrayList<>();

    Health health = healthEndpoint.invoke();
    Map<String, Object> healthDetails = health.getDetails();
    for (Map.Entry<String, Object> healthEntry : healthDetails.entrySet()) {
      String key = healthEntry.getKey();
      Object result = healthEntry.getValue();
      Boolean isHealthy = result.toString().contains("UP");
      if (isHealthy) {
        healthy.add(key);
      } else {
        unhealthy.add(key);
      }
    }

    if (!healthy.isEmpty()) {
      eventAttributes.put(HEALTHY_ATTRIBUTE, healthy.toString());
    }

    if (!unhealthy.isEmpty()) {
      eventAttributes.put(UNHEALTHY_ATTRIBUTE, unhealthy.toString());
    }

    NewRelic.getAgent().getInsights().recordCustomEvent(HEALTH_CHECK_EVENT, eventAttributes);
  }

}
