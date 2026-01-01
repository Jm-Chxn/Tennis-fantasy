-- Tennis Fantasy Database Schema
-- Compatible with PostgreSQL (Supabase) and H2

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    supabase_id VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255),
    avatar_url TEXT,
    total_points INTEGER DEFAULT 0,
    leagues_won INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- PLAYERS TABLE (Synced from SportsRadar)
-- =====================================================
CREATE TABLE IF NOT EXISTS players (
    id BIGSERIAL PRIMARY KEY,
    sportradar_id VARCHAR(255) UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    country_code VARCHAR(3),
    ranking INTEGER NOT NULL,
    points INTEGER,
    price DECIMAL(10, 2),
    tour VARCHAR(10),
    position VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    last_synced_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- LEAGUES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS leagues (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id BIGINT REFERENCES users(id),
    join_code VARCHAR(10) UNIQUE,
    max_teams INTEGER DEFAULT 8,
    current_teams INTEGER DEFAULT 0,
    roster_size INTEGER DEFAULT 8,
    starter_size INTEGER DEFAULT 5,
    draft_type VARCHAR(20) DEFAULT 'SNAKE',
    draft_status VARCHAR(20) DEFAULT 'NOT_STARTED',
    scoring_type VARCHAR(20) DEFAULT 'STANDARD',
    tour_type VARCHAR(10) DEFAULT 'BOTH',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    season INTEGER,
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    draft_date TIMESTAMP
);

-- =====================================================
-- LEAGUE MEMBERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS league_members (
    id BIGSERIAL PRIMARY KEY,
    league_id BIGINT NOT NULL REFERENCES leagues(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_name VARCHAR(255) NOT NULL,
    draft_position INTEGER,
    total_points INTEGER DEFAULT 0,
    weekly_points INTEGER DEFAULT 0,
    league_rank INTEGER,
    wins INTEGER DEFAULT 0,
    losses INTEGER DEFAULT 0,
    is_commissioner BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(league_id, user_id)
);

-- =====================================================
-- MATCHES TABLE (Synced from SportsRadar)
-- =====================================================
CREATE TABLE IF NOT EXISTS matches (
    id BIGSERIAL PRIMARY KEY,
    sportradar_id VARCHAR(255) UNIQUE,
    player1_id BIGINT REFERENCES players(id),
    player2_id BIGINT REFERENCES players(id),
    winner_id BIGINT REFERENCES players(id),
    score VARCHAR(100),
    player1_sets INTEGER,
    player2_sets INTEGER,
    tournament_name VARCHAR(255),
    tournament_id VARCHAR(255),
    round_name VARCHAR(100),
    match_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'scheduled',
    is_grand_slam BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- ROSTERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS rosters (
    id BIGSERIAL PRIMARY KEY,
    league_member_id BIGINT NOT NULL REFERENCES league_members(id) ON DELETE CASCADE,
    player_id BIGINT NOT NULL REFERENCES players(id),
    is_starter BOOLEAN DEFAULT TRUE,
    slot_number INTEGER,
    draft_round INTEGER,
    draft_pick INTEGER,
    points_earned INTEGER DEFAULT 0,
    acquisition_type VARCHAR(20) DEFAULT 'DRAFT',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(league_member_id, player_id)
);

-- =====================================================
-- FANTASY POINTS LOG TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS fantasy_points (
    id BIGSERIAL PRIMARY KEY,
    league_member_id BIGINT NOT NULL REFERENCES league_members(id) ON DELETE CASCADE,
    player_id BIGINT NOT NULL REFERENCES players(id),
    match_id BIGINT REFERENCES matches(id),
    points INTEGER NOT NULL,
    reason VARCHAR(100),
    description TEXT,
    tournament_name VARCHAR(255),
    week_number INTEGER,
    awarded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_players_tour ON players(tour);
CREATE INDEX IF NOT EXISTS idx_players_ranking ON players(ranking);
CREATE INDEX IF NOT EXISTS idx_players_sportradar_id ON players(sportradar_id);

CREATE INDEX IF NOT EXISTS idx_leagues_owner ON leagues(owner_id);
CREATE INDEX IF NOT EXISTS idx_leagues_status ON leagues(status);
CREATE INDEX IF NOT EXISTS idx_leagues_join_code ON leagues(join_code);

CREATE INDEX IF NOT EXISTS idx_league_members_league ON league_members(league_id);
CREATE INDEX IF NOT EXISTS idx_league_members_user ON league_members(user_id);

CREATE INDEX IF NOT EXISTS idx_matches_status ON matches(status);
CREATE INDEX IF NOT EXISTS idx_matches_date ON matches(match_date);
CREATE INDEX IF NOT EXISTS idx_matches_sportradar_id ON matches(sportradar_id);

CREATE INDEX IF NOT EXISTS idx_rosters_league_member ON rosters(league_member_id);
CREATE INDEX IF NOT EXISTS idx_rosters_player ON rosters(player_id);

CREATE INDEX IF NOT EXISTS idx_fantasy_points_member ON fantasy_points(league_member_id);
CREATE INDEX IF NOT EXISTS idx_fantasy_points_player ON fantasy_points(player_id);
CREATE INDEX IF NOT EXISTS idx_fantasy_points_match ON fantasy_points(match_id);
