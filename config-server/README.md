# CampusFlow Config Server

Minimal [Spring Cloud Config Server](https://docs.spring.io/spring-cloud-config/reference/server.html) for the CampusFlow companion repository.

This module demonstrates **centralized configuration** for Chapter 7 (Introducing Spring Cloud Components). It uses the **native filesystem backend** so the example stays small and runnable inside this repository.

> **Not wired into the monolith by default.** The monolith still starts with its local `application.yml`. Use the client section below when you want to try Config Server consumption.

## Layout

| Path | Role |
|------|------|
| `config-server/` | Spring Boot Config Server (`:8888`) |
| `config-repo/` | Ordinary folder with sample YAML (tracked in this repository) |

`config-repo/` is a normal directory in CampusFlow — not a separate repository. The Config Server reads files from that folder using the native backend. Readers do **not** need to initialize another repository.

## Prerequisites

- Java 21
- Maven 3.9+

## Run the Config Server

The default search location (`file:${user.dir}/../config-repo`) assumes the process is started from the `config-server/` directory:

```bash
cd config-server
mvn spring-boot:run
```

If you start from another working directory (IDE run configuration, packaged JAR, etc.), set an absolute or alternative filesystem location:

```bash
CONFIG_SEARCH_LOCATION="file:///absolute/path/to/Ultimate-Spring-Cloud-Native-for-Modern-Java-Apps/config-repo" \
  mvn spring-boot:run
```

Then verify:

```bash
curl http://localhost:8888/actuator/health
curl http://localhost:8888/campusflow-monolith/default
```

## How configuration is served

Config Server serves properties by **application name** and **profile**:

```text
/{application}/{profile}
```

Example:

```bash
curl http://localhost:8888/campusflow-monolith/default
```

Sample files in `config-repo/`:

| File | Served for |
|------|------------|
| `application.yml` | Shared defaults for all applications |
| `campusflow-monolith.yml` | Application-specific settings when `spring.application.name=campusflow-monolith` |

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
- Changing a file under `config-repo/` updates what the Config Server can serve next; it does **not** automatically push into an already running client.
- `/actuator/refresh`, `@RefreshScope`, and Spring Cloud Bus are intentionally **out of scope** for this minimal example.

### What this repository does *not* do yet

- The monolith is **not** connected to Config Server out of the box (quick start stays simple).
- No Vault, encryption, or HTTP security on the Config Server.
- No live configuration refresh for running clients.

Those topics belong to later infrastructure chapters.

> The native filesystem backend is used here to keep the Chapter 7 companion example small and runnable. Production systems commonly use a dedicated Git repository, Vault, or another managed configuration backend.

## Build and test

```bash
cd config-server
mvn verify
```

## Related documentation

- [docs/learning-path.md](../docs/learning-path.md) — chapter mapping (Ch. 4, 7)
- [docs/architecture.md](../docs/architecture.md) — where Config Server sits in the target picture
- Monolith local config: `monolith-baseline/src/main/resources/application.yml`
