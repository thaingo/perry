package gov.ca.cwds.security.module;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import gov.ca.cwds.security.AuthenticationException;
import gov.ca.cwds.security.authorizer.Authorizer;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import gov.ca.cwds.security.authorizer.StaticAuthorizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry.rudenko on 9/22/2017.
 */
public class SecurityModule extends AbstractModule {
  private Map<String, Class<? extends BaseAuthorizer>> authorizers = new HashMap<>();
  private Class<? extends StaticAuthorizer> staticAuthorizer;
  private static InjectorProvider injectorProvider;

  public SecurityModule(InjectorProvider injector) {
    injectorProvider = injector;
  }

  public static Injector injector() {
    if (injectorProvider == null) {
      throw new AuthenticationException("Security Module is not installed!");
    }
    return injectorProvider.getInjector();
  }

  @Override
  protected void configure() {
    authorizers.forEach((name, authorizerClass) -> bind(Authorizer.class)
        .annotatedWith(Names.named(name))
        .to(authorizerClass));
    if (staticAuthorizer != null) {
      bind(StaticAuthorizer.class).to(this.staticAuthorizer);
    } else {
      bind(StaticAuthorizer.class).toInstance((perryAccount, authorizationInfo) -> {
      });
    }
    bindInterceptor(
        Matchers.inSubpackage("gov.ca.cwds"),
        SecuredMethodMatcher.hasAuthorizeAnnotation(), new AbacMethodInterceptor()
    );
  }

  public SecurityModule addAuthorizer(String permission, Class<? extends BaseAuthorizer> clazz) {
    authorizers.put(permission, clazz);
    return this;
  }

  public SecurityModule setStaticAuthorizer(Class<? extends StaticAuthorizer> clazz) {
    this.staticAuthorizer = clazz;
    return this;
  }
}
