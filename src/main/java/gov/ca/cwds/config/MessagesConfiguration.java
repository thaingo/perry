package gov.ca.cwds.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessagesConfiguration {

  @Bean
  @Qualifier("tech")
  public MessageSource getTechMessageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:/messages/tech_messages");
    messageSource.setAlwaysUseMessageFormat(true);
    return messageSource;
  }

  @Bean
  @Qualifier("user")
  public MessageSource getUserMessageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:/messages/user_messages");
    messageSource.setAlwaysUseMessageFormat(true);
    return messageSource;
  }

}
