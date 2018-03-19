package gov.ca.cwds.security.test;

import gov.ca.cwds.security.realm.PerryRealm;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 * @author CWDS CALS API Team
 */

public class TestRealm extends PerryRealm {

  @Override
  protected String validate(String token) {
    try {
      try (InputStream principalJson = getClass().getClassLoader().getResourceAsStream(token)) {
        return IOUtils.toString(principalJson, "UTF-8");
      }
    } catch (IOException e) {
      throw new IllegalStateException("Can't get principal", e);
    }
  }

}
