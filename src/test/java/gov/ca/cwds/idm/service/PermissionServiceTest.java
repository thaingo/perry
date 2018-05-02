package gov.ca.cwds.idm.service;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.idm.BaseTokenStoreLiquibaseTest;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PermissionServiceTest extends BaseTokenStoreLiquibaseTest {

  @Autowired
  private PermissionService permissionService;

  @Test
  public void testGetPermissionNames() {
    List<String> permissionNames = permissionService.getPermissionNames();
    assertThat(permissionNames, notNullValue());
    assertThat(permissionNames, hasSize(5));

    List<String> expectedValues = Arrays.asList(
        "cals-core-county",
        "cals-core-user",
        "development-not-in-use",
        "intake-core-county",
        "intake-core-user"
    );
    assertEquals(expectedValues, permissionNames);
  }
}
