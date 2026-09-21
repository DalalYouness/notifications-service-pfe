# Notifications Service

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-event--driven-black?logo=apachekafka)](https://kafka.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8+-blue?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/license-academic-lightgrey.svg)](#license)

A production-oriented notification microservice for a distributed home-services platform. The service consumes business events from Apache Kafka, persists user notifications in MySQL, and exposes secured REST endpoints for clients and service providers to consult and manage their notifications.

This project is part of a Master's final project focused on building a scalable, modular, and resilient home-services platform based on a distributed-systems architecture.

## Contents

- [Overview](#overview)
- [Key capabilities](#key-capabilities)
- [Architecture](#architecture)
- [Technology stack](#technology-stack)
- [Event-driven workflow](#event-driven-workflow)
- [REST API](#rest-api)
- [Project structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Run locally](#run-locally)
- [Testing](#testing)
- [Observability and documentation](#observability-and-documentation)
- [Security](#security)
- [Related services](#related-services)
- [Contributing](#contributing)
- [License](#license)

## Overview

The Notifications Service centralizes notification creation and delivery data for the platform. Instead of tightly coupling reservation, review, and notification logic, upstream services publish domain events and this service reacts asynchronously.

This approach provides:

- Loose coupling between business services.
- Asynchronous processing through Kafka.
- Durable notification history in MySQL.
- A single API for unread counts, notification history, and notification lifecycle actions.
- Service discovery through Netflix Eureka.
- Centralized configuration through Spring Cloud Config.

## Key capabilities

- Consume reservation-created events.
- Consume reservation-status-updated events.
- Consume review-created events.
- Create tailored notifications for platform users and providers.
- Retrieve a user's notifications, ordered from newest to oldest.
- Return the unread notification count used by client badges.
- Mark individual notifications as read.
- Delete one notification or clear all notifications for a user.
- Apply database migrations automatically with Flyway.
- Validate and document HTTP APIs with Springdoc OpenAPI.
- Expose health and operational endpoints through Spring Boot Actuator.

## Architecture

```text
                    +--------------------------+
                    | Reservation / Review    |
                    | business microservices  |
                    +------------+-------------+
                                 |
                         Domain events via Kafka
                                 |
              +------------------v------------------+
              |       Notifications Service         |
              |                                      |
              | Kafka consumers -> domain handlers   |
              | REST API -> notification management  |
              | Security -> JWT/RBAC protection      |
              +------------------+-------------------+
                                 |
                         +-------v-------+
                         |     MySQL     |
                         | notification  |
                         |    records    |
                         +---------------+

 Supporting infrastructure: Config Server, Eureka, Kafka, and API Gateway
```

The service follows a layered structure:

- **Web layer**: secured REST controllers and HTTP response models.
- **Service layer**: notification creation and business operations.
- **Messaging layer**: Kafka consumers for domain events.
- **Persistence layer**: JPA entities and repositories.
- **Security layer**: JWT-based authentication and role-based authorization.
- **Migration layer**: Flyway versioned database schema.

## Technology stack

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Application framework | Spring Boot 3.5 |
| Web API | Spring Web, Spring Validation, Springdoc OpenAPI |
| Persistence | Spring Data JPA, Hibernate, MySQL |
| Database migrations | Flyway |
| Messaging | Apache Kafka, Spring Kafka |
| Service discovery | Netflix Eureka Client |
| Centralized configuration | Spring Cloud Config Client |
| Security | Spring Security, JWT |
| Object mapping | MapStruct |
| Boilerplate reduction | Lombok |
| Build tool | Maven Wrapper |
| Operational monitoring | Spring Boot Actuator |

## Event-driven workflow

The service currently listens to the following Kafka topics:

| Topic | Event | Result |
| --- | --- | --- |
| `reservation-created-topic` | `ReservationCreatedEvent` | Creates a notification related to a new reservation. |
| `reservation-status-updated-topic` | `ReservationStatusUpdatedEvent` | Creates a notification when a reservation status changes. |
| `review-created-topic` | `ReviewCreatedEvent` | Creates a notification related to a newly submitted review. |

A typical flow is:

1. A business microservice changes domain state.
2. The business microservice publishes a domain event to Kafka.
3. The Notifications Service consumes the event.
4. The event is translated into a user-facing notification.
5. The notification is persisted in MySQL.
6. A client retrieves it through the REST API.

## REST API

The base path is `/api/v1/notifications`. Endpoints require an authenticated user with the `CLIENT` or `PROVIDER` role.

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/user/{userId}` | Retrieve all notifications for a user. |
| `GET` | `/user/{userId}/unread-count` | Retrieve the number of unread notifications. |
| `PATCH` | `/{id}/read` | Mark a notification as read. |
| `DELETE` | `/{id}` | Delete one notification. |
| `DELETE` | `/user/{userId}` | Delete all notifications belonging to a user. |

Interactive API documentation is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

The exact host and port can be changed through the active Spring configuration.

## Project structure

```text
src/
├── main/
│   ├── java/com/dalal/notificationsservicepfe/
│   │   ├── config/          # Application and infrastructure configuration
│   │   ├── dtos/             # Request, response, and event DTOs
│   │   ├── entities/         # JPA persistence entities
│   │   ├── enums/            # Notification-related enumerations
│   │   ├── exceptions/       # Domain and application exceptions
│   │   ├── feign/            # Declarative HTTP clients
│   │   ├── handler/          # Error handling and API error responses
│   │   ├── mappers/          # MapStruct mappings
│   │   ├── messaging/        # Kafka event consumers
│   │   ├── repositories/     # Spring Data repositories
│   │   ├── security/         # JWT and authorization configuration
│   │   ├── services/         # Notification business logic
│   │   └── web/              # REST controllers
│   └── resources/
│       ├── db/migration/     # Flyway SQL migrations
│       └── application.properties
└── test/                     # Automated tests
```

## Prerequisites

Install the following before running the service:

- Java Development Kit 21.
- Git.
- Docker and Docker Compose, recommended for infrastructure dependencies.
- MySQL 8 or a compatible MySQL instance.
- Apache Kafka and its required broker dependencies.
- The platform's Config Server, available at `http://localhost:8888` by default.
- A running Eureka Server if service registration is enabled in the shared configuration.

## Configuration

The service imports configuration from Spring Cloud Config by default:

```properties
spring.application.name=notifications-service-pfe
spring.config.import=optional:configserver:http://localhost:8888
```

Before starting the application, configure the following values in the shared configuration or an environment-specific profile:

- MySQL JDBC URL, username, and password.
- Kafka bootstrap servers, consumer group, and JSON deserialization settings.
- Eureka server URL.
- JWT verification key and security settings.
- Application port and actuator exposure.

Do not commit private keys, passwords, tokens, or production credentials. Prefer environment variables or a secrets manager. The repository's authentication material should be replaced with environment-specific configuration for deployment.

## Run locally

### 1. Clone the repository

```bash
git clone https://github.com/DalalYouness/notifications-service-pfe.git
cd notifications-service-pfe
```

### 2. Start infrastructure

Start MySQL, Kafka, the Config Server, and Eureka using your platform infrastructure setup. Confirm that the Config Server is reachable at `http://localhost:8888` or update `spring.config.import` accordingly.

### 3. Build the project

Linux/macOS:

```bash
./mvnw clean verify
```

Windows:

```powershell
.\mvnw.cmd clean verify
```

### 4. Start the service

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Flyway applies the notification schema on startup when the database connection is correctly configured.

## Testing

Run the full test suite with:

```bash
./mvnw test
```

For a complete verification package, including compilation and integration checks:

```bash
./mvnw clean verify
```

## Observability and documentation

Spring Boot Actuator is included for health and operational monitoring. The exact exposed endpoints are controlled by the active configuration.

Springdoc OpenAPI generates API documentation automatically. With the service running, open:

```text
http://localhost:8080/swagger-ui/index.html
```

## Security

The API is protected with Spring Security and JWT-based authentication. Access to notification operations is intended for authenticated platform users with the appropriate role:

- `CLIENT`
- `PROVIDER`

In a production deployment:

- Use HTTPS between clients, gateways, and services.
- Store JWT keys and database credentials outside source control.
- Restrict actuator endpoints.
- Configure Kafka authentication and encryption where required.
- Validate that a caller can access only notifications belonging to the authenticated user.

## Related services

This microservice is designed to operate as part of the broader home-services platform, alongside services such as:

- Reservation or booking management.
- User and identity management.
- Provider and service management.
- Review and rating management.
- API Gateway.
- Config Server.
- Service Registry / Eureka Server.

Topic names and event contracts must remain compatible with the producing services.

## Contributing

1. Create a feature branch from the default branch.
2. Implement the change following the existing package and naming conventions.
3. Add or update tests where appropriate.
4. Run `./mvnw clean verify`.
5. Open a pull request with a clear description of the change and its impact on event contracts or APIs.

## License

This repository is an academic Master's final-project component. Add the project's formal license and attribution details here when they are finalized.

---

Built as part of a distributed home-services platform Master's project.
