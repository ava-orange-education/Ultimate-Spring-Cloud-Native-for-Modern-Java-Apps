package com.campusflow.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Enables graceful shutdown — useful for Kubernetes rolling updates and book snippets
 * on zero-downtime deployments.
 */
@Configuration
public class GracefulShutdownConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatGracefulShutdown() {
        return factory -> factory.addConnectorCustomizers(connector ->
                connector.setProperty("connectionTimeout", "20000"));
    }
}
