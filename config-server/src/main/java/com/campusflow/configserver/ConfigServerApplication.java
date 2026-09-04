package com.campusflow.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Minimal Spring Cloud Config Server for CampusFlow.
 *
 * <p>Serves externalized configuration from the native filesystem backend
 * ({@code config-repo/} at the repository root for local development).
 * Later chapters can discuss Git, Vault, security, and refresh strategies.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
