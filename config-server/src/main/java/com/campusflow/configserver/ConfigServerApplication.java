package com.campusflow.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Minimal Spring Cloud Config Server for CampusFlow.
 *
 * <p>Serves externalized configuration from a Git-backed repository
 * ({@code config-repo/} at the repository root for local development).
 * Later chapters can extend this with security, encryption, and remote remotes.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
