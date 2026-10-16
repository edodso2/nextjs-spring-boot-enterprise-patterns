# Users Service

A simple Spring Boot application for user management.

## Getting Started

### Install Java

I recommend using [SDKMAN!](https://sdkman.io/) to manage Java installations:

1. Install SDKMAN! following the instructions at https://sdkman.io/
2. Install Java 21:
   ```bash
   sdk install java 21.0.8-amzn
   ```
3. Set it as default when prompted

### Run the Application

1. Navigate to the project directory:

   ```bash
   cd ./users-service
   ```

2. Start the database from the repository root:

   ```bash
   docker compose up -d
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

The application will start at http://localhost:8080 and apply any pending Flyway migrations. Verify it is running by opening `http://localhost:8080/actuator/health`. You should see `{"status":"UP"}`.

### Run the Tests

The tests start a throwaway Postgres container with Testcontainers, so Docker must be running:

```bash
./gradlew test
```

## Developer Experience

Spring Boot provides an excellent developer experience similar to Node.js, with features like:

- Hot reloading during development
- Embedded server
- Minimal configuration required

## Troubleshooting

If you encounter issues with SDKMAN! installation or Java setup, consult the [SDKMAN! documentation](https://sdkman.io/usage) or search for your specific error message.
