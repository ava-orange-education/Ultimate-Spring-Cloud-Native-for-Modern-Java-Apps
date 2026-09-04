package com.campusflow.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerApplicationTest {

    static {
        try {
            ensureLocalConfigRepoIsAGitRepository();
        } catch (IOException | InterruptedException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void servesMonolithConfigurationFromGitBackend() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/campusflow-monolith/default", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("campusflow-monolith");
        assertThat(response.getBody()).contains("school-name");
    }

    private static void ensureLocalConfigRepoIsAGitRepository() throws IOException, InterruptedException {
        Path configRepo = Path.of("..", "config-repo").toAbsolutePath().normalize();
        assertThat(Files.isDirectory(configRepo))
                .as("Expected local config-repo at %s", configRepo)
                .isTrue();

        if (Files.exists(configRepo.resolve(".git"))) {
            return;
        }

        run(configRepo, "git", "init", "-b", "main");
        run(configRepo, "git", "config", "user.email", "campusflow@example.com");
        run(configRepo, "git", "config", "user.name", "CampusFlow");
        run(configRepo, "git", "add", ".");
        run(configRepo, "git", "commit", "-m", "Initial CampusFlow configuration");
    }

    private static void run(Path workingDir, String... command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command)
                .directory(workingDir.toFile())
                .redirectErrorStream(true)
                .start();
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        assertThat(finished).as("Command timed out: %s", String.join(" ", command)).isTrue();
        String output = new String(process.getInputStream().readAllBytes());
        assertThat(process.exitValue())
                .as("Command failed: %s%n%s", String.join(" ", command), output)
                .isZero();
    }
}
