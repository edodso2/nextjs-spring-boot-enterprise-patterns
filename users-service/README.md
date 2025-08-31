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

2. Run the application:
   ```bash
   ./gradlew bootRun
   ```

The application will start and be available at the default Spring Boot port (typically http://localhost:8080). Verify the app is running by opening `http://localhost:8080/api/health-check`. You should see `OK` on the screen.

## Developer Experience

Spring Boot provides an excellent developer experience similar to Node.js, with features like:

- Hot reloading during development
- Embedded server
- Minimal configuration required

## Troubleshooting

If you encounter issues with SDKMAN! installation or Java setup, consult the [SDKMAN! documentation](https://sdkman.io/usage) or search for your specific error message.
