package com.campusflow.configserver;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
class ConfigServerApplicationTest {

    private static Path temporaryConfigRepo;

    @DynamicPropertySource
    static void registerTemporaryGitBackend(DynamicPropertyRegistry registry) throws Exception {
        temporaryConfigRepo = createTemporaryGitConfigRepository();
        registry.add("spring.cloud.config.server.git.uri", () -> temporaryConfigRepo.toUri().toString());
        registry.add("spring.cloud.config.server.git.default-label", () -> "main");
        registry.add("spring.cloud.config.server.git.clone-on-start", () -> "true");
    }

    @AfterAll
    static void deleteTemporaryGitConfigRepository() throws IOException {
        if (temporaryConfigRepo == null || !Files.exists(temporaryConfigRepo)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(temporaryConfigRepo)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // best-effort cleanup of the disposable test repository
                }
            });
        }
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void servesSharedAndApplicationSpecificConfigurationFromGitBackend() {
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

    private static Map<String, Object> flattenPropertySources(Environment environment) {
        Map<String, Object> properties = new LinkedHashMap<>();
        for (PropertySource propertySource : environment.getPropertySources()) {
            for (Map.Entry<?, ?> entry : propertySource.getSource().entrySet()) {
                properties.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return properties;
    }

    private static Path createTemporaryGitConfigRepository() throws Exception {
        Path repoDir = Files.createTempDirectory("campusflow-config-server-test-");

        Files.writeString(repoDir.resolve("application.yml"), """
                campusflow:
                  notifications:
                    from-address: noreply@campusflow.example
                """);

        Files.writeString(repoDir.resolve("campusflow-monolith.yml"), """
                campusflow:
                  school-name: CampusFlow Academy (Config Server)
                  features:
                    attendance-reminders: true
                    enrollment-confirmation: true
                """);

        PersonIdent author = new PersonIdent("CampusFlow", "campusflow@example.com");
        try (Git git = Git.init().setDirectory(repoDir.toFile()).setInitialBranch("main").call()) {
            git.add().addFilepattern(".").call();
            git.commit()
                    .setMessage("Initial CampusFlow configuration")
                    .setAuthor(author)
                    .setCommitter(author)
                    .call();
        }

        return repoDir;
    }
}
