# Supabase Integration Setup Guide

This guide will help you set up your Java Spring Boot backend to:
1. Call the SportsRadar API to get player data
2. Filter and process the data in Java
3. Post the filtered data to Supabase (no SQL in Java code)

## Prerequisites

1. A Supabase account and project
2. Your Supabase project URL and anon key
3. SportsRadar API access (you'll add this later)
4. Java 17+ and Maven installed

## Step 1: Set Up Supabase Database

1. Go to your Supabase project dashboard
2. Navigate to the SQL Editor
3. Copy and paste the contents of `supabase-setup.sql` into the editor
4. Run the script to create the `players` table

**Note**: This is the ONLY SQL you'll need to run. Your Java code will never write SQL - it will only make REST API calls to Supabase.

## Step 2: Verify Configuration

Ensure your `application.properties` has the correct Supabase credentials:

```properties
supabase.url=https://your-project-id.supabase.co
supabase.anon.key=your-anon-key-here
```

## Step 3: Build and Run the Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8081/api` (changed from 8080 to avoid conflicts)

## Step 4: Test the Integration

### Test 1: Check Supabase Connection
```bash
curl http://localhost:8081/api/players/test-connection
```

Expected response: `Supabase connection test: [{"count":0}]` (or similar)

### Test 2: Create a Test Player (Simulating SportsRadar Data)
```bash
curl -X POST http://localhost:8081/api/supabase-test/create-test-player
```

Expected response: `Test player created successfully with ID: [some-number]`

### Test 3: Get All Players from Supabase
```bash
curl http://localhost:8081/api/supabase-test/players
```

Expected response: `Found [number] players`

### Test 4: Create a Player via Main API (Simulating SportsRadar Data)
```bash
curl -X POST http://localhost:8081/api/players \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Roger",
    "lastName": "Federer",
    "country": "Switzerland",
    "ranking": 4,
    "points": 9500,
    "price": 12.50,
    "position": "Singles",
    "isActive": true
  }'
```

Expected response: The created player object with an ID

### Test 5: Get Players by Country
```bash
curl http://localhost:8081/api/supabase-test/players/country/Serbia
```

Expected response: `Found 1 players from Serbia`

### Test 6: Test SportsRadar Integration (when you have API key)
```bash
# Test SportsRadar connection
curl http://localhost:8081/api/sportsradar/test-connection

# Fetch raw data from SportsRadar
curl http://localhost:8081/api/sportsradar/fetch-players

# Complete flow: SportsRadar → Filter → Supabase
curl -X POST http://localhost:8081/api/sportsradar/process-and-save
```

## Step 5: Monitor Supabase

1. Go to your Supabase dashboard
2. Navigate to Table Editor > players
3. You should see the new players being added when you test the APIs

## How It Works

### Architecture Flow:
```
SportsRadar API → Java Backend → Supabase
     ↓              ↓           ↓
  Raw Data    Filter/Process   Store
```

### Java Code Responsibilities:
- **NO SQL** - Your Java code never writes SQL
- **API Calls** - Makes HTTP requests to SportsRadar API
- **Data Processing** - Filters, transforms, and validates player data
- **Supabase Integration** - Posts processed data to Supabase via REST API

### Supabase Responsibilities:
- **Database Hosting** - All SQL and data storage
- **REST API** - Provides endpoints for your Java backend to call
- **Authentication** - Handles user access and security
- **Real-time Features** - Can notify your frontend of data changes

## Next Steps for SportsRadar Integration

1. **Add SportsRadar API Configuration**:
   ```properties
   sportsradar.api.key=your-api-key-here
   sportsradar.api.base-url=https://api.sportsradar.com
   ```

2. **Create SportsRadar Service**:
   - HTTP client to call SportsRadar API
   - Data parsing and filtering logic
   - Integration with existing PlayerService

3. **Implement Data Flow**:
   - Fetch players from SportsRadar
   - Apply your filtering logic
   - Post filtered players to Supabase
   - Schedule regular updates

## API Endpoints

### Main Player Endpoints:
- `GET /api/players` - Get all players from Supabase
- `POST /api/players` - Create a new player in Supabase
- `GET /api/players/{id}` - Get player by ID from Supabase
- `PUT /api/players/{id}` - Update player in Supabase
- `DELETE /api/players/{id}` - Delete player from Supabase

### Test Endpoints:
- `GET /api/supabase-test/connection` - Test Supabase connection
- `POST /api/supabase-test/create-test-player` - Create test player
- `GET /api/supabase-test/players` - Get all players (test)
- `GET /api/supabase-test/players/country/{country}` - Get players by country (test)

### SportsRadar Integration Endpoints:
- `GET /api/sportsradar/test-connection` - Test SportsRadar API connection
- `GET /api/sportsradar/fetch-players` - Fetch raw data from SportsRadar
- `POST /api/sportsradar/process-and-save` - Complete flow: SportsRadar → Filter → Supabase
- `POST /api/sportsradar/sync-players` - Manual trigger to sync players

## Troubleshooting

### Common Issues:
1. **Connection Failed**: Check your Supabase URL and anon key
2. **CORS Errors**: Ensure your frontend origin is allowed in Supabase
3. **Table Not Found**: Make sure you ran the SQL setup script
4. **Permission Denied**: Check Row Level Security policies in Supabase

### Debug Steps:
1. Check the backend logs for detailed error messages
2. Verify the Supabase table structure matches your Player model
3. Test the Supabase REST API directly using tools like Postman
4. Check the Network tab in your browser's DevTools for API calls

## Security Notes

- The current setup allows all operations on the players table
- In production, implement proper Row Level Security policies
- Consider using service role keys for admin operations
- Implement proper authentication and authorization
