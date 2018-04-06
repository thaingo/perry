package gov.ca.cwds.service.oauth;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class CognitoOAuth2RestTemplateServiceTest {


  @Test
  public void testAugmentMessageConvertersReturnMessageConverterListOfCorrectSize() {
    CognitoOAuth2RestTemplateService service = new CognitoOAuth2RestTemplateService();
    service.setApiVersion("apiversion");

    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    List<HttpMessageConverter<?>> originalConverters = new ArrayList<HttpMessageConverter<?>>();
    originalConverters.add(converter);

    List<HttpMessageConverter<?>> converters = service.augmentMessageConverters(originalConverters);

    assertThat(converters.size(), equalTo(1));
  }

  @Test
  public void testAugmentMessageConvertersAddsCorrectMessageConverter() {
    CognitoOAuth2RestTemplateService service = new CognitoOAuth2RestTemplateService();
    service.setApiVersion("apiversion");

    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    List<HttpMessageConverter<?>> originalConverters = new ArrayList<HttpMessageConverter<?>>();
    originalConverters.add(converter);

    List<HttpMessageConverter<?>> converters = service.augmentMessageConverters(originalConverters);
    MappingJackson2HttpMessageConverter jsonConverter =
        (MappingJackson2HttpMessageConverter) converters.get(0);
    assertThat(jsonConverter.getSupportedMediaTypes().contains(new MediaType("application",
        "apiversion", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET)), equalTo(true));
  }

}
