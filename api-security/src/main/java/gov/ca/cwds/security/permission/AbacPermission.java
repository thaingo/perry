package gov.ca.cwds.security.permission;

import com.google.inject.Key;
import com.google.inject.name.Names;
import gov.ca.cwds.security.authorizer.Authorizer;
import gov.ca.cwds.security.module.SecurityModule;
import java.util.Collection;
import org.apache.shiro.authz.Permission;

import java.util.Optional;

/**
 * Created by dmitry.rudenko on 9/25/2017.
 */

public class AbacPermission implements Permission {
  private Object securedObject;

  private static final String SUPPORTED_PERMISSION_PATTERN = "^[^:,*]+:[^:,*]+:[^:,*]+$";
  private Authorizer authorizer;
  private static final String PARTS_DELIMITER = ":";

  public AbacPermission() {

  }

  public AbacPermission(String permissionString) {
    if (permissionString.matches(SUPPORTED_PERMISSION_PATTERN)) {
      String[] parts = permissionString.split(PARTS_DELIMITER);
      String handlerName = parts[0] + PARTS_DELIMITER + parts[1];
      Optional<Authorizer> permissionHandler = findPermissionHandler(handlerName);
      if (!permissionHandler.isPresent()) {
        throw new IllegalArgumentException();
      }
      this.securedObject = parts[2];
      this.authorizer = permissionHandler.get();
    } else {
      throw new IllegalArgumentException();
    }
  }

  public Object getSecuredObject() {
    return securedObject;
  }

  public void setSecuredObject(Object securedObject) {
    this.securedObject = securedObject;
  }

  @Override
  public boolean implies(Permission permission) {
    if (!(permission instanceof AbacPermission)) {
      return false;
    }
    AbacPermission abacPermission = (AbacPermission) permission;
    return abacPermission.check();
  }

  private boolean check() {
    if (securedObject == null) {
      return true;
    } else if (authorizer == null) {
      return false;
    } else if (securedObject instanceof Collection) {
      filterCollection();
      return true;
    } else {
      return authorizer.check(securedObject);
    }
  }


  @SuppressWarnings("unchecked")
  private void filterCollection() {
    Collection securedCollection = (Collection) securedObject;
    Collection filteredCollection = authorizer.filter(securedCollection);
    if (securedCollection.size() != filteredCollection.size()) {
      securedCollection.clear();
      securedCollection.addAll(filteredCollection);
    }
  }

  private Optional<Authorizer> findPermissionHandler(String name) {
    try {
      Authorizer authorizer = SecurityModule.injector().getInstance(Key.get(Authorizer.class, Names.named(name)));
      return Optional.of(authorizer);
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
