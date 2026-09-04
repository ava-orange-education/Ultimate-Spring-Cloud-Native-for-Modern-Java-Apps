# CampusFlow Config Server

Minimal [Spring Cloud Config Server](https://docs.spring.io/spring-cloud-config/reference/server.html) for the CampusFlow companion repository.

This module demonstrates **centralized, Git-backed configuration** for Chapter 7 (Introducing Spring Cloud Components). It is intentionally small: later chapters can add security, encryption, remote Git remotes, and refresh strategies.

> **Not wired into the monolith by default.** The monolith still starts with its local `application.yml`. Use the client section below when you want to try Config Server consumption.

## Layout

| Path | Role |
|------|------|
| `config-server/` | Spring Boot Config Server (`:8888`) |
| `config-repo/` | Local Git configuration repository (sample YAML) |

## Prerequisites

- Java 21
- Maven 3.9+
- Git

## One-time: initialize the local config Git repository

Spring Cloud Config’s Git backend expects a real Git repository. From the repository root:

```bash
cd config-repo
git init -b main
git add .
git -c user.name="CampusFlow" -c user.email="campusflow@example.com" \
  commit -m "Initial CampusFlow configuration"
cd ..
```

The `-c user.name` / `-c user.email` options set identity for this commit only, so you do not need a global Git user configured.

You only need to do this once on your machine (or after a fresh clone). Automated tests use an isolated temporary Git repository and do **not** modify `config-repo/`.

### Verify the local repository

```bash
cd config-repo
git branch --show-current    # expect: main
git rev-list --count HEAD    # expect: at least 1
cd ..
```

## Run the Config Server

The default Git URI (`file://${user.dir}/../config-repo`) assumes the process is started from the `config-server/` directory:

```bash
cd config-server
mvn spring-boot:run
```

If you start from another working directory (IDE run configuration, packaged JAR, etc.), set an absolute URI:

```bash
CONFIG_GIT_URI="file:///absolute/path/to/Ultimate-Spring-Cloud-Native-for-Modern-Java-Apps/config-repo" \
  mvn spring-boot:run
```

Health check:

```bash
curl http://localhost:8888/actuator/health
```

## Fetch configuration

Config Server serves properties by **application name**, **profile**, and **label** (Git branch/tag):

```text
/{application}/{profile}[/{label}]
```

Examples:

```bash
# Shared + monolith-specific properties (default profile, main label)
curl http://localhost:8888/campusflow-monolith/default

# Explicit label
curl http://localhost:8888/campusflow-monolith/default/main
```

Sample files in `config-repo/`:

| File | Served for |
|------|------------|
| `application.yml` | All applications |
| `campusflow-monolith.yml` | `spring.application.name=campusflow-monolith` |

## How CampusFlow clients consume Config Server (Config Data API)

Spring Boot 2.4+ uses the **Config Data API** (`spring.config.import`). Prefer this over the legacy `bootstrap.yml` / `spring-cloud-starter-bootstrap` approach.

### 1. Add the client dependency

In the consuming service (for example `monolith-baseline/pom.xml`):

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

Import the Spring Cloud BOM (same train as the Config Server, e.g. `2023.0.x`) if the module does not already manage Spring Cloud versions.

### 2. Import the Config Server

In the client `application.yml` (or `application.properties`):

```yaml
spring:
  application:
    name: campusflow-monolith
  config:
    import: "optional:configserver:http://localhost:8888"
```

Notes:

- `optional:` keeps the app startable when the Config Server is down (useful while learning).
- Remove `optional:` when Config Server must be required.
- Property sources from the Config Server override matching keys from the local `application.yml` according to Spring Boot’s usual precedence rules.

### 3. Keep using typed configuration

CampusFlow already binds `campusflow.*` through `AppProperties`. No change is required there — Config Server simply becomes another property source for the same keys (`campusflow.school-name`, feature flags, and so on).

### Runtime behaviour (Chapter 7 scope)

- Clients load configuration from Config Server during **startup** (Config Data loading).
- Committing a change in `config-repo/` updates what the Config Server can serve next; it does **not** automatically push into an already running client.
- `/actuator/refresh`, `@RefreshScope`, and Spring Cloud Bus are intentionally **out of scope** for this minimal example.

### What this repository does *not* do yet

- The monolith is **not** connected to Config Server out of the box (quick start stays simple).
- No Vault, encryption, or HTTP security on the Config Server.
- No live configuration refresh for running clients.

Those topics belong to later infrastructure chapters.

## Build and test

```bash
cd config-server
mvn verify
```

## Related documentation

- [docs/learning-path.md](../docs/learning-path.md) — chapter mapping (Ch. 4, 7)
- [docs/architecture.md](../docs/architecture.md) — where Config Server sits in the target picture
- Monolith local config: `monolith-baseline/src/main/resources/application.yml`
