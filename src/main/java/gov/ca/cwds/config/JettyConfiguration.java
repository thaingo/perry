package gov.ca.cwds.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dmitry.rudenko on 6/23/2017.
 */
@Configuration
public class JettyConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(JettyConfiguration.class);

  @Value("${server.http.port:-1}")
  private int httpPort;

  @Bean
  public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
    return new EmbeddedServletContainerCustomizer() {

      @Override
      public void customize(ConfigurableEmbeddedServletContainer container) {
        if (container instanceof JettyEmbeddedServletContainerFactory) {
          customizeJetty((JettyEmbeddedServletContainerFactory) container);
        } else if (container instanceof TomcatEmbeddedServletContainerFactory) {
          customizeTomcat((TomcatEmbeddedServletContainerFactory) container);
        }
      }

      private void customizeJetty(JettyEmbeddedServletContainerFactory container) {
        if (httpPort > 0) {
          enableHttpPort(container);
        }
      }

      private void customizeTomcat(TomcatEmbeddedServletContainerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
          connector.setAllowTrace(false);
        });
      }

      private void enableHttpPort(JettyEmbeddedServletContainerFactory container) {
        container.addServerCustomizers((JettyServerCustomizer) server -> {
          // HTTP
          final ServerConnector connector = new ServerConnector(server);
          connector.setPort(httpPort);
          server.addConnector(connector);

          LOGGER.info("\n********* Jetty server version: {} *********", Server.getVersion());
        });
      }
    };
  }

}
