package gov.ca.cwds.config.nostate;

import gov.ca.cwds.config.logging.DebugRestTemplateInterceptor;
import org.junit.Test;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;

import java.lang.reflect.Field;
import java.util.List;

@SuppressWarnings("unchecked")
public class NoStateUserInfoRestTemplateCustomizerTest {
  @Test
  public void testCustomize() throws Exception {
    NoStateUserInfoRestTemplateCustomizer noStateUserInfoRestTemplateCustomizer = new NoStateUserInfoRestTemplateCustomizer();
    OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(new AuthorizationCodeResourceDetails());
    noStateUserInfoRestTemplateCustomizer.customize(oAuth2RestTemplate);
    AccessTokenProviderChain accessTokenProvider = getFieldValue(oAuth2RestTemplate, "accessTokenProvider", AccessTokenProviderChain.class);
    assert accessTokenProvider != null;
    List list = getFieldValue(accessTokenProvider, "chain", List.class);
    assert list.size() == 4;
    assert list.stream().anyMatch(o -> o instanceof ImplicitAccessTokenProvider);
    assert list.stream().anyMatch(o -> o instanceof ResourceOwnerPasswordAccessTokenProvider);
    assert list.stream().anyMatch(o -> o instanceof ClientCredentialsAccessTokenProvider);
    assert list.stream().anyMatch(o -> o instanceof NoStateAuthorizationCodeTokenProvider);
    NoStateAuthorizationCodeTokenProvider noStateAuthorizationCodeTokenProvider =
        (NoStateAuthorizationCodeTokenProvider) list.stream()
            .filter(o -> o instanceof NoStateAuthorizationCodeTokenProvider)
            .findFirst().get();
    assert !getFieldValue(noStateAuthorizationCodeTokenProvider, "stateMandatory", Boolean.class);
    List interceptors = getFieldValue(noStateAuthorizationCodeTokenProvider, "interceptors", List.class);
    assert interceptors.size() == 1;
    assert interceptors.stream().anyMatch(o -> o instanceof DebugRestTemplateInterceptor);
  }

  @SuppressWarnings("unchecked")
  private static <T> T getFieldValue(Object object, String fieldName, Class<T> fieldType) throws Exception {
    Field field = getField(object.getClass(), fieldName);
    field.setAccessible(true);
    return (T) field.get(object);
  }

  private static Field getField(Class clazz, String name) throws NoSuchFieldException {
    try {
      return clazz.getDeclaredField(name);
    } catch (NoSuchFieldException e) {
      Class superClass = clazz.getSuperclass();
      if(superClass != null) {
        return getField(superClass, name);
      }
      throw e;
    }
  }
}
