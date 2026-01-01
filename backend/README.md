# Tennis Fantasy Backend

Spring Boot backend for the Tennis Fantasy application.

## Running Locally

```bash
# Using Maven Wrapper (recommended)
./mvnw spring-boot:run

# Or using installed Maven
mvn spring-boot:run
```

Server starts at: `http://localhost:8080/api`

## Test the API

```bash
# Initialize sample players
curl -X POST http://localhost:8080/api/sportradar/init-sample

# Get all players
curl http://localhost:8080/api/players

# Search players
curl "http://localhost:8080/api/players/search?name=djokovic"
```

## H2 Console (Development)

Access the H2 database console at: `http://localhost:8080/api/h2-console`

- JDBC URL: `jdbc:h2:mem:tennisfantasy`
- Username: `sa`
- Password: `password`

## Configuration

Edit `src/main/resources/application.properties` to configure:

- Database connection
- SportsRadar API key
- JWT secret for authentication

## Using PostgreSQL (Supabase)

Set these environment variables:

```bash
export SUPABASE_DB_URL=jdbc:postgresql://your-project.supabase.co:5432/postgres
export SUPABASE_DB_USER=postgres
export SUPABASE_DB_PASSWORD=your-password
export DB_DRIVER=org.postgresql.Driver
export JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

Then run:
```bash
./mvnw spring-boot:run
```
