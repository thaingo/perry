package gov.ca.cwds.smoketest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by dmitry.rudenko on 8/24/2017.
 */
public class SmokeTest {
  private static final long RETRY_PERIOD = 5 * 1000 * 60;//5 minutes
  private static final long RETRY_TIMEOUT = 20 * 1000; //20 seconds;

  @Test
  public void test() throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();
    String result = getHealth(restTemplate);
    Map map = mapper.readValue(result, Map.class);
    if ("DOWN".equals(map.get("status"))) {
      throw new Exception(result);
    }
  }

  @SuppressWarnings({"squid:S2925"})
  private String getHealth(RestTemplate restTemplate) throws Exception {
    String url = System.getProperty("perry.health.check.url");
    long endTime = System.currentTimeMillis() + RETRY_PERIOD;
    while (System.currentTimeMillis() < endTime) {
      try {
        return restTemplate.getForObject(url, String.class);
      } catch (Exception e) {
        Thread.sleep(RETRY_TIMEOUT);
      }
    }
    throw new Exception("CAN'T CONNECT TO: " + url);
  }
}
