# Next.js and Spring Boot Enterprise Patterns

This blog series is targeted at Full Stack Software Engineers interested in using the most popular enterprise frameworks. Regardless of personal opinions and trends Spring Boot continues to rain supreme in backend enterprise while React positions itself to be the entprise standard on front end for years to come with Next.js quickly emerging as the all in one framework for React, similar to how Spring Boot did for Java a few years ago. 

The `main` branch is a full stack enterprise starter: a running microservice backend, a database, and a front end administration console, all runnable with one command. Each other branch starts from `main` and adds one system design pattern (job queues, caching, load balancing, read replicas, and so on) so you can build it, break it, and measure it hands on.

## Architecture

| Container | What it is | Port |
| --- | --- | --- |
| `users-db` | Postgres, owned only by the users service (database per service) | 5432 |
| `users-service` | Spring Boot REST API for managing users | 8080 |
| `admin-console` | Next.js admin UI that calls the users service from the server | 3000 |

Branches add containers alongside these, for example a new microservice with its own database, a worker, a cache, or a load balancer.

## Running the Project

### Quick start: everything in Docker

The only prerequisite is [Docker](https://docs.docker.com/get-docker/). Use this to try the app, and for load tests, failure experiments, and scaling.

```bash
docker compose --profile app up --build
```

Open http://localhost:3000/users. Stop everything with `docker compose --profile app down` (add `-v` to delete the database volume).

### Dev mode: infrastructure in Docker, apps on your machine

Use this while writing code so you keep hot reload and your debugger. In addition to Docker you need Java 21 and Node.js 22; see the [users-service](users-service/README.md) and [admin-console](admin-console/README.md) READMEs for setup.

```bash
docker compose up -d                            # starts users-db only
cd users-service && ./gradlew bootRun           # http://localhost:8080
cd admin-console && npm install && npm run dev  # http://localhost:3000
```

### Configuration

Services are configured through environment variables, so the same image runs anywhere:

| Service | Variable | Local default |
| --- | --- | --- |
| `users-service` | `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | `jdbc:postgresql://localhost:5432/users`, `users`, `users` |
| `admin-console` | `USERS_SERVICE_URL` | `http://localhost:8080` |

The database schema is managed by Flyway migrations in `users-service/src/main/resources/db/migration`. Health and metrics are exposed by Spring Boot Actuator at `/actuator/health` and `/actuator/metrics`.

## Branches

Each branch of this repository will focus on a specific enterprise pattern such as caching, code generation, etc. 

### Build Once Run Anywhere With Next.js New cacheComponents Feature

The `next-api-dynamic-io` branch covers the new `cacheComponents` feature and how it supports the build once run anywhere paradigm. 

### Building a Complete SDK Pipeline: Spring Boot OpenAPI to Next.js TypeScript

Coming Soon!
