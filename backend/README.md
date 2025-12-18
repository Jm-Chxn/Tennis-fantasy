# Tennis Fantasy Backend

A Spring Boot backend application for the Tennis Fantasy application.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd Tennis-fantasy/backend
```

### 2. Build the project
```bash
mvn clean install
```

### 3. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Players

- `GET /api/players` - Get all players
- `GET /api/players/{id}` - Get player by ID
- `POST /api/players` - Create new player
- `PUT /api/players/{id}` - Update existing player
- `DELETE /api/players/{id}` - Delete player

### Player Queries

- `GET /api/players/country/{country}` - Get players by country
- `GET /api/players/position/{position}` - Get players by position
- `GET /api/players/active` - Get active players
- `GET /api/players/ranking?minRanking=X&maxRanking=Y` - Get players by ranking range
- `GET /api/players/search?name=X` - Search players by name
- `GET /api/players/top-ranked` - Get top ranked players
- `GET /api/players/price-range?minPrice=X&maxPrice=Y` - Get players by price range
- `GET /api/players/count/country/{country}` - Get player count by country

### Data Initialization

- `POST /api/players/init-sample-data` - Initialize with sample tennis players

## Database

The application uses H2 in-memory database for development. You can access the H2 console at:
`http://localhost:8080/api/h2-console`

- JDBC URL: `jdbc:h2:mem:tennisfantasy`
- Username: `sa`
- Password: `password`

## Sample Data

The application comes with sample tennis players including:
- Novak Djokovic (Serbia, Rank 1)
- Carlos Alcaraz (Spain, Rank 2)
- Daniil Medvedev (Russia, Rank 3)
- Iga Swiatek (Poland, Rank 1)
- Aryna Sabalenka (Belarus, Rank 2)

## Configuration

Key configuration properties in `application.properties`:
- Server port: 8080
- API context path: `/api`
- CORS enabled for `http://localhost:3000` (frontend)
- JWT secret and expiration settings
- H2 database configuration

## Dependencies

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- Spring Web
- H2 Database
- JWT for authentication
- Validation

## Development

### Project Structure
```
src/main/java/com/tennisfantasy/backend/
├── TennisFantasyBackendApplication.java  # Main application class
├── controller/                           # REST controllers
├── service/                             # Business logic services
├── repository/                          # Data access layer
└── model/                              # Entity classes

src/main/resources/
└── application.properties               # Configuration file
```

### Adding New Features

1. Create entity classes in the `model` package
2. Create repository interfaces in the `repository` package
3. Create service classes in the `service` package
4. Create controllers in the `controller` package
5. Update configuration as needed

## Testing

Run tests with:
```bash
mvn test
```

## Building for Production

```bash
mvn clean package
```

The JAR file will be created in the `target` directory.

## Troubleshooting

- Ensure Java 17+ is installed and JAVA_HOME is set
- Check that port 8080 is available
- Verify Maven is properly installed
- Check application logs for detailed error messages
