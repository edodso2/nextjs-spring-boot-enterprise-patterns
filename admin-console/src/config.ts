// Server-only configuration, read at runtime so one image can run in any environment.
// Inside Docker Compose this is http://users-service:8080; locally it falls back to localhost.
export const usersServiceUrl =
  process.env.USERS_SERVICE_URL ?? 'http://localhost:8080';
