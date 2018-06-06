package gov.ca.cwds.management;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class AppVersionInfoContributor implements InfoContributor {

  @Value("build.number")
  private String buildNumber;

  @Value("build.version")
  private String buildVersion;

  @Override
  public void contribute(Info.Builder builder) {

    builder.withDetail("buildNumber", buildNumber)
            .withDetail("buildVersion", buildVersion);
  }
}
