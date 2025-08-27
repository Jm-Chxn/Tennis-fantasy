# Tennis Fantasy Backend Architecture Overview

## System Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  SportsRadar   │    │   Java Backend   │    │    Supabase     │
│      API       │───▶│   (Spring Boot)  │───▶│   (Database)    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
       │                        │                        │
       │                        │                        │
       ▼                        ▼                        ▼
   Raw Player Data        Filtered/Processed        Stored Players
                        Player Data in Java
```

## Component Responsibilities

### 1. SportsRadar API
- **Purpose**: External data source for tennis player information
- **Data**: Raw player statistics, rankings, match results
- **Access**: REST API with authentication key
- **Format**: JSON responses

### 2. Java Backend (Spring Boot)
- **Purpose**: Data processing and business logic
- **Responsibilities**:
  - Fetch data from SportsRadar API
  - Apply filtering criteria
  - Transform data to match your data model
  - Send processed data to Supabase
  - **NO SQL** - Only REST API calls

### 3. Supabase
- **Purpose**: Database hosting and data storage
- **Responsibilities**:
  - Store processed player data
  - Provide REST API for data access
  - Handle authentication and authorization
  - Real-time data synchronization

## Data Flow

### Step 1: Data Fetching
```java
// SportsRadarService.fetchTennisPlayers()
// Makes HTTP GET request to SportsRadar API
// Returns raw JSON response
```

### Step 2: Data Processing
```java
// SportsRadarService.parseAndFilterPlayers()
// 1. Parse JSON response
// 2. Convert to Player objects
// 3. Apply filtering criteria
// 4. Return filtered players
```

### Step 3: Data Storage
```java
// SupabaseService.createPlayer()
// Makes HTTP POST request to Supabase REST API
// Stores player in database
```

## Key Benefits of This Architecture

### 1. **Separation of Concerns**
- **SportsRadar**: Data source only
- **Java Backend**: Business logic and data processing
- **Supabase**: Data storage and API

### 2. **No SQL in Java Code**
- All database operations via REST API calls
- Easy to maintain and debug
- No SQL injection risks

### 3. **Scalability**
- Can easily add more data sources
- Can implement caching strategies
- Can add more processing logic

### 4. **Flexibility**
- Easy to change filtering criteria
- Easy to modify data transformation logic
- Easy to add new data fields

## Implementation Details

### SportsRadar Integration
- **Service**: `SportsRadarService`
- **HTTP Client**: WebClient (reactive)
- **Configuration**: API key in `application.properties`
- **Error Handling**: Graceful fallbacks

### Supabase Integration
- **Service**: `SupabaseService`
- **HTTP Client**: WebClient (reactive)
- **Configuration**: URL and anon key in `application.properties`
- **Data Model**: Matches your Player entity

### Data Processing
- **Filtering**: Customizable criteria in `applyFilterCriteria()`
- **Transformation**: Data mapping in `convertToPlayer()`
- **Validation**: Business rule enforcement
- **Pricing**: Fantasy sports pricing model

## Configuration Files

### application.properties
```properties
# Supabase Configuration
supabase.url=https://your-project.supabase.co
supabase.anon.key=your-anon-key

# SportsRadar Configuration
sportsradar.api.key=your-api-key
sportsradar.api.base-url=https://api.sportsradar.com
```

### Database Schema (supabase-setup.sql)
- Creates `players` table
- Sets up indexes for performance
- Enables Row Level Security
- Creates automatic timestamp updates

## Testing Strategy

### 1. **Unit Tests**
- Test individual service methods
- Mock external API calls
- Verify business logic

### 2. **Integration Tests**
- Test complete data flow
- Verify Supabase integration
- Test error scenarios

### 3. **API Tests**
- Test all REST endpoints
- Verify data formats
- Test error responses

## Deployment Considerations

### 1. **Environment Variables**
- Store API keys securely
- Use different configs for dev/staging/prod
- Never commit secrets to version control

### 2. **Monitoring**
- Log API call responses
- Monitor error rates
- Track data processing performance

### 3. **Scheduling**
- Implement regular data sync
- Handle API rate limits
- Implement retry logic

## Future Enhancements

### 1. **Caching**
- Cache SportsRadar responses
- Implement Redis for session data
- Cache frequently accessed players

### 2. **Real-time Updates**
- Use Supabase real-time features
- WebSocket connections for live data
- Push notifications for updates

### 3. **Advanced Filtering**
- Machine learning for player selection
- Dynamic pricing algorithms
- Performance analytics

## Security Considerations

### 1. **API Security**
- Secure API key storage
- Rate limiting
- Request validation

### 2. **Data Security**
- Row Level Security in Supabase
- Input sanitization
- Output encoding

### 3. **Access Control**
- Authentication middleware
- Role-based permissions
- Audit logging

This architecture provides a clean, maintainable, and scalable solution for your tennis fantasy application while keeping all SQL operations in Supabase and focusing your Java code on business logic and data processing.

