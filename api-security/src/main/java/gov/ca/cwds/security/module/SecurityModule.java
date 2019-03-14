package gov.ca.cwds.security.module;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import gov.ca.cwds.security.AuthenticationException;
import gov.ca.cwds.security.authorizer.Authorizer;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import gov.ca.cwds.security.authorizer.DefaultStaticAuthorizer;
import gov.ca.cwds.security.authorizer.StaticAuthorizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by dmitry.rudenko on 9/22/2017.
 */
public class SecurityModule extends AbstractModule {
  private Map<String, Class<? extends BaseAuthorizer>> authorizers;
  private Set<Class<? extends StaticAuthorizer>> staticAuthorizers;
  private static InjectorProvider injectorProvider;

  public SecurityModule(InjectorProvider injector) {
    authorizers = new HashMap<>();
    staticAuthorizers = new HashSet<>();
    staticAuthorizers.add(DefaultStaticAuthorizer.class);
  }

  public static Injector injector() {
    if (injectorProvider == null) {
      throw new AuthenticationException("Security Module is not installed!");
    }
    return injectorProvider.getInjector();
  }

  public static Set<StaticAuthorizer> getStaticAuthorizers() {
    return injectorProvider.getInjector().getInstance(Key.get(setOf(StaticAuthorizer.class)));
  }

  @SuppressWarnings("unchecked")
  private static <T> TypeLiteral<Set<T>> setOf(Class<T> type) {
    return (TypeLiteral<Set<T>>) TypeLiteral.get(Types.setOf(type));
  }

  @Override
  protected void configure() {
    authorizers.forEach((name, authorizerClass) -> bind(Authorizer.class)
        .annotatedWith(Names.named(name))
        .to(authorizerClass));

    Multibinder<StaticAuthorizer> multibinder = Multibinder.newSetBinder(
        binder(),
        StaticAuthorizer.class
    );
    staticAuthorizers.forEach(staticAuthorizer -> multibinder.addBinding().to(staticAuthorizer));

    bindInterceptor(
        Matchers.inSubpackage("gov.ca.cwds"),
        SecuredMethodMatcher.hasAuthorizeAnnotation(), new AbacMethodInterceptor()
    );
  }

  public SecurityModule addAuthorizer(String permission, Class<? extends BaseAuthorizer> clazz) {
    authorizers.put(permission, clazz);
    return this;
  }

  public SecurityModule addStaticAuthorizer(Class<? extends StaticAuthorizer> clazz) {
    staticAuthorizers.add(clazz);
    return this;
  }
}
