// API Utilities for Tennis Fantasy
// Base URL for backend API

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// Generic fetch wrapper with error handling
async function fetchApi<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const url = `${API_BASE_URL}${endpoint}`;

    // Get current auth token
    const token = await import('./supabase').then(m => m.supabaseAuth.getToken());

    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        ...((options.headers as Record<string, string>) || {}),
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
        ...options,
        headers,
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'API Error' }));
        throw new Error(error.error || error.message || 'Request failed');
    }

    return response.json();
}

// Player types
export interface Player {
    id: number;
    sportradarId: string;
    firstName: string;
    lastName: string;
    country: string;
    countryCode: string;
    ranking: number;
    points: number;
    price: number;
    tour: string;
    position: string;
    isActive: boolean;
}

// League types
export interface League {
    id: number;
    name: string;
    description: string;
    joinCode: string;
    maxTeams: number;
    currentTeams: number;
    rosterSize: number;
    draftType: string;
    draftStatus: string;
    tourType: string;
    status: string;
}

export interface LeagueMember {
    id: number;
    teamName: string;
    totalPoints: number;
    weeklyPoints: number;
    leagueRank: number;
    wins: number;
    losses: number;
}

// API Functions

// Players
export const playersApi = {
    getAll: () => fetchApi<Player[]>('/players'),
    getById: (id: number) => fetchApi<Player>(`/players/${id}`),
    getByTour: (tour: string) => fetchApi<Player[]>(`/players/tour/${tour}`),
    search: (name: string) => fetchApi<Player[]>(`/players/search?name=${name}`),
    initSampleData: () => fetchApi<{ message: string }>('/sportradar/init-sample', { method: 'POST' }),
};

// Leagues
export const leaguesApi = {
    getAll: () => fetchApi<League[]>('/leagues'),
    getPublic: () => fetchApi<League[]>('/leagues/public'),
    getById: (id: number) => fetchApi<League>(`/leagues/${id}`),
    create: (data: { name: string; userId: number; teamName: string; maxTeams?: number; tourType?: string }) =>
        fetchApi<League>('/leagues', { method: 'POST', body: JSON.stringify(data) }),
    join: (data: { joinCode: string; userId: number; teamName: string }) =>
        fetchApi<LeagueMember>('/leagues/join', { method: 'POST', body: JSON.stringify(data) }),
    getMembers: (leagueId: number) => fetchApi<LeagueMember[]>(`/leagues/${leagueId}/members`),
    getStandings: (leagueId: number) => fetchApi<LeagueMember[]>(`/leagues/${leagueId}/standings`),
};

// Draft
export const draftApi = {
    getStatus: (leagueId: number) => fetchApi<{ status: string }>(`/leagues/${leagueId}/draft/status`),
    start: (leagueId: number) =>
        fetchApi(`/leagues/${leagueId}/draft/start`, { method: 'POST' }),
    makePick: (leagueId: number, leagueMemberId: number, playerId: number) =>
        fetchApi(`/leagues/${leagueId}/draft/pick`, {
            method: 'POST',
            body: JSON.stringify({ leagueMemberId, playerId }),
        }),
    getAvailablePlayers: (leagueId: number) => fetchApi<Player[]>(`/leagues/${leagueId}/draft/available`),
};

// Roster
export const rosterApi = {
    get: (leagueId: number, userId: number) => fetchApi(`/leagues/${leagueId}/roster?userId=${userId}`),
    getStarters: (leagueId: number, userId: number) =>
        fetchApi(`/leagues/${leagueId}/roster/starters?userId=${userId}`),
    updateLineup: (leagueId: number, userId: number, starterIds: number[], benchIds: number[]) =>
        fetchApi(`/leagues/${leagueId}/roster`, {
            method: 'PUT',
            body: JSON.stringify({ userId, starterIds, benchIds }),
        }),
    getBudget: (leagueId: number, userId: number) =>
        fetchApi(`/leagues/${leagueId}/roster/budget?userId=${userId}`),
    addPlayer: (leagueId: number, userId: number, playerId: number) =>
        fetchApi(`/leagues/${leagueId}/roster/add`, {
            method: 'POST',
            body: JSON.stringify({ userId, playerId }),
        }),
    remove: (leagueId: number, userId: number, playerId: number) =>
        fetchApi(`/leagues/${leagueId}/roster/remove`, {
            method: 'DELETE',
            body: JSON.stringify({ userId, playerId }),
        }),
};

// Scoring
export const scoringApi = {
    getScoringRules: (leagueId: number) => fetchApi<Record<string, number>>(`/leagues/${leagueId}/scoring-rules`),
    getPoints: (leagueId: number, userId: number) => fetchApi(`/leagues/${leagueId}/points?userId=${userId}`),
};

// Auth (syncs with backend)
export const authApi = {
    register: (data: { supabaseId: string; email: string; displayName: string }) =>
        fetchApi('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
    getProfile: (supabaseId: string) => fetchApi(`/auth/profile?supabaseId=${supabaseId}`),
    updateProfile: (data: { supabaseId: string; displayName?: string; avatarUrl?: string }) =>
        fetchApi('/auth/profile', { method: 'PUT', body: JSON.stringify(data) }),
};

// SportsRadar data
export const sportsRadarApi = {
    getRankings: () => fetchApi('/sportradar/rankings'),
    getAtpRankings: (limit = 50) => fetchApi(`/sportradar/rankings/atp?limit=${limit}`),
    getWtaRankings: (limit = 50) => fetchApi(`/sportradar/rankings/wta?limit=${limit}`),
    getLiveScores: () => fetchApi('/sportradar/live'),
    getTodaySchedule: () => fetchApi('/sportradar/schedule/today'),
};
