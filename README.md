# Tennis Fantasy 🎾🏆

A beginner-friendly fantasy tennis league web application where you can draft real ATP/WTA players, build your roster, and earn points based on their tournament performances.

## Tech Stack

- **Frontend**: Next.js 15 + React 19 + TypeScript + Tailwind CSS
- **Backend**: Java 17 + Spring Boot 3.2
- **Database**: PostgreSQL (Supabase) or H2 (development)
- **Authentication**: Supabase Auth
- **External Data**: SportsRadar Tennis API v3

## Quick Start

### Prerequisites

- Node.js 18+
- Java 17+
- Maven 3.8+
- (Optional) SportsRadar API key for live data

### 1. Start the Backend

```bash
cd backend

# Run with default H2 database (easiest for development)
./mvnw spring-boot:run
```

The backend will start at `http://localhost:8080/api`

### 2. Initialize Sample Data

Open a new terminal and run:
```bash
curl -X POST http://localhost:8080/api/sportradar/init-sample
```

This loads 20 sample players (10 ATP, 10 WTA) for testing.

### 3. Start the Frontend

```bash
cd frontend

npm install
npm run dev
```

The frontend will start at `http://localhost:3000`

### 4. Open the App

Visit `http://localhost:3000` in your browser!

## API Endpoints

### Players
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/players` | Get all players |
| GET | `/api/players/{id}` | Get player by ID |
| GET | `/api/players/search?name=X` | Search players |
| POST | `/api/sportradar/init-sample` | Initialize sample data |

### Leagues
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/leagues` | Get all leagues |
| POST | `/api/leagues` | Create a league |
| POST | `/api/leagues/join` | Join via code |
| GET | `/api/leagues/{id}/standings` | Get standings |

### Draft
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/leagues/{id}/draft/start` | Start draft |
| POST | `/api/leagues/{id}/draft/pick` | Make a pick |
| GET | `/api/leagues/{id}/draft/status` | Get draft status |

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register user |
| GET | `/api/auth/profile?supabaseId=X` | Get profile |

## Scoring System

| Action | Points |
|--------|--------|
| Match Win | +10 |
| Grand Slam Match Win | +15 |
| Set Win | +2 |
| Upset Bonus | +5 |
| Tournament Win | +25 |
| Grand Slam Title | +50 |

## Environment Variables

### Backend (`application.properties`)

```properties
# Use H2 for local development (default)
# For Supabase PostgreSQL:
SUPABASE_DB_URL=jdbc:postgresql://YOUR_PROJECT.supabase.co:5432/postgres
SUPABASE_DB_USER=postgres
SUPABASE_DB_PASSWORD=your-password
DB_DRIVER=org.postgresql.Driver
JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# SportsRadar API (optional - for live data)
SPORTRADAR_API_KEY=your-api-key

# Supabase JWT Secret (for auth)
SUPABASE_JWT_SECRET=your-jwt-secret
```

### Frontend (`.env.local`)

```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_SUPABASE_URL=your-supabase-url
NEXT_PUBLIC_SUPABASE_ANON_KEY=your-anon-key
```

## Project Structure

```
tennis-fantasy/
├── backend/
│   └── src/main/java/com/tennisfantasy/backend/
│       ├── config/          # Spring configs
│       ├── controller/      # REST endpoints
│       ├── dto/              # Data transfer objects
│       ├── model/           # JPA entities
│       ├── repository/      # Database queries
│       ├── security/        # JWT filter
│       └── service/         # Business logic
├── frontend/
│   └── src/
│       ├── app/             # Next.js pages
│       ├── contexts/        # React contexts
│       └── lib/             # Utilities
└── README.md
```

## Features

- ✅ Player drafting with snake draft format
- ✅ Real-time scoring based on match results  
- ✅ League creation and management
- ✅ User authentication with Supabase
- ✅ SportsRadar API integration for live data
- ✅ Responsive UI with Tailwind CSS

## Getting SportsRadar API Key

1. Go to [developer.sportradar.com](https://developer.sportradar.com)
2. Create a free account
3. Subscribe to Tennis API v3 (Trial tier is free)
4. Copy your API key to `application.properties`

## License

This project is for personal and educational use.
