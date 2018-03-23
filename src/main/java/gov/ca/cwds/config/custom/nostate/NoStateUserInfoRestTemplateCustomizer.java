package gov.ca.cwds.config.custom.nostate;

import gov.ca.cwds.config.logging.DebugRestTemplateInterceptor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by TPT2 on 1/10/2018.
 */
@Component
@Profile("nostate")
public class NoStateUserInfoRestTemplateCustomizer implements UserInfoRestTemplateCustomizer {

  @Override
  public void customize(OAuth2RestTemplate template) {
    AuthorizationCodeAccessTokenProvider noStateAuthorizationCodeTokenProvider = new NoStateAuthorizationCodeTokenProvider();
    noStateAuthorizationCodeTokenProvider.setInterceptors(Arrays.asList(new DebugRestTemplateInterceptor()));
    noStateAuthorizationCodeTokenProvider.setStateMandatory(false);
    template.setAccessTokenProvider(new AccessTokenProviderChain(Arrays.<AccessTokenProvider>asList(
        noStateAuthorizationCodeTokenProvider, new ImplicitAccessTokenProvider(),
        new ResourceOwnerPasswordAccessTokenProvider(), new ClientCredentialsAccessTokenProvider())));
  }
}
