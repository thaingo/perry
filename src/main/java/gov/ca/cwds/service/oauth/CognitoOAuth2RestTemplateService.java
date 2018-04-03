package gov.ca.cwds.service.oauth;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@Profile("prod")
@Primary
@ConfigurationProperties(prefix = "cognito")
public class CognitoOAuth2RestTemplateService extends OAuth2RestTemplateService {

  private String apiVersion;

  @Override
  protected List<HttpMessageConverter<?>> augmentMessageConverters(
      List<HttpMessageConverter<?>> messageConverters) {
    for (HttpMessageConverter<?> converter : messageConverters) {
      if (converter instanceof MappingJackson2HttpMessageConverter) {
        MappingJackson2HttpMessageConverter jsonConverter =
            (MappingJackson2HttpMessageConverter) converter;
        jsonConverter.setObjectMapper(new ObjectMapper());
        jsonConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("application", apiVersion,
            MappingJackson2HttpMessageConverter.DEFAULT_CHARSET)));
      }
    }
    return messageConverters;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

}
