package com.campusflow.configserver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("native")
class ConfigServerApplicationTest {

    private static Path temporaryConfigDir;

    @DynamicPropertySource
    static void registerTemporaryNativeBackend(DynamicPropertyRegistry registry) throws Exception {
        temporaryConfigDir = createTemporaryConfigDirectory();
        registry.add(
                "spring.cloud.config.server.native.search-locations",
                () -> temporaryConfigDir.toUri().toString());
    }

    @AfterAll
    static void deleteTemporaryConfigDirectory() throws IOException {
        if (temporaryConfigDir == null || !Files.exists(temporaryConfigDir)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(temporaryConfigDir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // best-effort cleanup of the disposable test directory
                }
            });
        }
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void servesSharedAndApplicationSpecificConfigurationFromNativeBackend() {
        ResponseEntity<Environment> response =
                restTemplate.getForEntity("/campusflow-monolith/default", Environment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("campusflow-monolith");

        Map<String, Object> properties = flattenPropertySources(response.getBody());
        assertThat(properties.get("campusflow.school-name"))
                .isEqualTo("CampusFlow Academy (Config Server)");
        assertThat(properties.get("campusflow.notifications.from-address"))
                .isEqualTo("noreply@campusflow.example");
    }

    /**
     * Config Server returns property sources highest-priority first. Use putIfAbsent so
     * earlier (higher-priority) values are kept.
     */
    private static Map<String, Object> flattenPropertySources(Environment environment) {
        Map<String, Object> properties = new LinkedHashMap<>();
        for (PropertySource propertySource : environment.getPropertySources()) {
            for (Map.Entry<?, ?> entry : propertySource.getSource().entrySet()) {
                properties.putIfAbsent(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return properties;
    }

    private static Path createTemporaryConfigDirectory() throws IOException {
        Path configDir = Files.createTempDirectory("campusflow-config-server-test-");

        Files.writeString(configDir.resolve("application.yml"), """
                campusflow:
                  notifications:
                    from-address: noreply@campusflow.example
                """);

        Files.writeString(configDir.resolve("campusflow-monolith.yml"), """
                campusflow:
                  school-name: CampusFlow Academy (Config Server)
                  features:
                    attendance-reminders: true
                    enrollment-confirmation: true
                """);

        return configDir;
    }
}
