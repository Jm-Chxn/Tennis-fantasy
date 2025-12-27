'use client';

import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'next/navigation';
import { playersApi, rosterApi, leaguesApi, authApi, Player, League } from '@/lib/api';
import { useAuth } from '@/contexts/AuthContext';
import Link from 'next/link';

interface BudgetInfo {
  budget: number;
  currentRosterSize: number;
  maxRosterSize: number;
  teamName: string;
}

export default function PlayersPage() {
  const searchParams = useSearchParams();
  const leagueIdParam = searchParams.get('leagueId');
  const leagueId = leagueIdParam ? Number(leagueIdParam) : null;

  const [players, setPlayers] = useState<Player[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTour, setSelectedTour] = useState<'all' | 'ATP' | 'WTA'>('all');
  const [initialized, setInitialized] = useState(false);

  // League/Roster state
  const [league, setLeague] = useState<League | null>(null);
  const [budgetInfo, setBudgetInfo] = useState<BudgetInfo | null>(null);
  const [myRosterPlayerIds, setMyRosterPlayerIds] = useState<Set<number>>(new Set());
  const [addingPlayer, setAddingPlayer] = useState<number | null>(null);

  const { user, loading: authLoading } = useAuth();
  const [backendUserId, setBackendUserId] = useState<number | null>(null);

  useEffect(() => {
    const fetchBackendUser = async () => {
      if (user?.id) {
        try {
          const profile = await authApi.getProfile(user.id);
          setBackendUserId(profile.id);
        } catch (err) {
          console.error('Failed to fetch user profile', err);
        }
      }
    };
    fetchBackendUser();
  }, [user]);

  // Use backendUserId instead of 1
  const userId = backendUserId;

  // Fetch players on mount
  useEffect(() => {
    fetchPlayers();
  }, []);

  // Fetch league data if leagueId is provided
  const fetchLeagueData = useCallback(async () => {
    if (!leagueId) return;

    try {
      const [leagueData, budgetData, rosterData] = await Promise.all([
        leaguesApi.getById(leagueId),
        rosterApi.getBudget(leagueId, userId),
        rosterApi.get(leagueId, userId)
      ]);
      setLeague(leagueData);
      setBudgetInfo(budgetData as BudgetInfo);

      // Build set of player IDs already on roster
      const rosterPlayerIds = new Set<number>();
      (rosterData as { player: Player }[]).forEach((r) => {
        rosterPlayerIds.add(r.player.id);
      });
      setMyRosterPlayerIds(rosterPlayerIds);
    } catch (err) {
      console.error('Error fetching league data:', err);
    }
  }, [leagueId, userId]);

  useEffect(() => {
    fetchLeagueData();
  }, [fetchLeagueData]);

  const fetchPlayers = async () => {
    setLoading(true);
    try {
      const data = await playersApi.getAll();
      setPlayers(data);
      setInitialized(data.length > 0);
    } catch (err) {
      setError('Failed to fetch players. Make sure the backend is running.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const initSampleData = async () => {
    try {
      await playersApi.initSampleData();
      fetchPlayers();
    } catch (err) {
      setError('Failed to initialize sample data');
    }
  };

  const addToRoster = async (playerId: number) => {
    if (!leagueId) return;

    setAddingPlayer(playerId);
    try {
      await rosterApi.addPlayer(leagueId, userId, playerId);
      // Refresh budget and roster data
      await fetchLeagueData();
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to add player';
      alert(errorMessage);
    } finally {
      setAddingPlayer(null);
    }
  };

  // Filter players based on search and tour
  const filteredPlayers = players.filter(player => {
    const matchesSearch =
      `${player.firstName} ${player.lastName}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
      player.country.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesTour = selectedTour === 'all' || player.tour === selectedTour;
    return matchesSearch && matchesTour;
  });

  // Tour button style
  const tourButtonClass = (tour: string) => `
    px-4 py-2 rounded-lg font-medium transition-all duration-200
    ${selectedTour === tour
      ? 'bg-green-600 text-white shadow-lg'
      : 'bg-white/10 text-green-100 hover:bg-white/20'}
  `;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900">
      {/* Header */}
      <header className="bg-black/30 backdrop-blur-lg border-b border-white/10">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-3xl">🎾</span>
            <Link href="/" className="text-2xl font-bold text-white">Tennis Fantasy</Link>
          </div>
          <nav className="flex gap-4">
            <Link href="/dashboard" className="text-green-200 hover:text-white transition-colors">Dashboard</Link>
            <Link href="/leagues" className="text-green-200 hover:text-white transition-colors">Leagues</Link>
            <Link href="/leaderboard" className="text-green-200 hover:text-white transition-colors">Leaderboard</Link>
          </nav>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        {/* League Context Bar */}
        {leagueId && budgetInfo && (
          <div className="mb-6 bg-gradient-to-r from-yellow-600/20 to-amber-600/20 rounded-xl p-4 border border-yellow-500/30">
            <div className="flex flex-wrap items-center justify-between gap-4">
              <div>
                <p className="text-yellow-200 text-sm">Adding to: <span className="text-white font-bold">{league?.name || 'League'}</span></p>
                <p className="text-yellow-100 text-xs">Team: {budgetInfo.teamName}</p>
              </div>
              <div className="flex items-center gap-6">
                <div>
                  <p className="text-yellow-200 text-xs">Budget</p>
                  <p className="text-2xl font-bold text-white">💰 ${budgetInfo.budget?.toFixed(2)}</p>
                </div>
                <div>
                  <p className="text-yellow-200 text-xs">Roster</p>
                  <p className="text-2xl font-bold text-white">{budgetInfo.currentRosterSize}/{budgetInfo.maxRosterSize}</p>
                </div>
                <Link
                  href={`/leagues/${leagueId}/roster`}
                  className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors text-sm"
                >
                  View Roster →
                </Link>
              </div>
            </div>
          </div>
        )}

        {/* Title */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-white mb-2">Player Database</h1>
          <p className="text-green-200">Browse ATP and WTA players for your fantasy team</p>
        </div>

        {/* Filters */}
        <div className="flex flex-wrap gap-4 mb-8">
          {/* Search */}
          <div className="flex-1 min-w-[200px]">
            <input
              type="text"
              placeholder="Search players..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full px-4 py-3 rounded-lg bg-white/10 border border-white/20 text-white placeholder-green-200 focus:outline-none focus:ring-2 focus:ring-green-400"
            />
          </div>

          {/* Tour Filter */}
          <div className="flex gap-2">
            <button
              onClick={() => setSelectedTour('all')}
              className={tourButtonClass('all')}
            >
              All Tours
            </button>
            <button
              onClick={() => setSelectedTour('ATP')}
              className={tourButtonClass('ATP')}
            >
              ATP
            </button>
            <button
              onClick={() => setSelectedTour('WTA')}
              className={tourButtonClass('WTA')}
            >
              WTA
            </button>
          </div>
        </div>

        {/* Content */}
        {loading ? (
          <div className="text-center py-16">
            <div className="text-6xl mb-4 animate-bounce">🎾</div>
            <p className="text-green-200 text-xl">Loading players...</p>
          </div>
        ) : error ? (
          <div className="text-center py-16">
            <div className="text-6xl mb-4">⚠️</div>
            <p className="text-red-300 text-xl mb-4">{error}</p>
            <p className="text-green-200">Start the backend server with: <code className="bg-black/30 px-2 py-1 rounded">cd backend && ./mvnw spring-boot:run</code></p>
          </div>
        ) : !initialized ? (
          <div className="text-center py-16 bg-white/5 rounded-2xl border border-white/10">
            <div className="text-6xl mb-4">📋</div>
            <h2 className="text-2xl font-bold text-white mb-2">No Players Yet</h2>
            <p className="text-green-200 mb-6">Initialize sample data to get started</p>
            <button
              onClick={initSampleData}
              className="px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-500 text-white font-semibold rounded-lg hover:from-green-600 hover:to-emerald-600 transition-all shadow-lg"
            >
              Load Sample Players
            </button>
          </div>
        ) : (
          <>
            <p className="text-green-200 mb-4">
              Showing {filteredPlayers.length} of {players.length} players
            </p>

            {/* Players Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {filteredPlayers.map((player) => (
                <div
                  key={player.id}
                  className="bg-white/5 backdrop-blur-lg rounded-xl p-6 border border-white/10 hover:border-green-500/50 transition-all duration-200 hover:transform hover:scale-[1.02]"
                >
                  {/* Player Header */}
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <span className={`inline-block px-2 py-1 rounded text-xs font-bold ${player.tour === 'ATP'
                        ? 'bg-blue-500/20 text-blue-300'
                        : 'bg-pink-500/20 text-pink-300'
                        }`}>
                        {player.tour}
                      </span>
                    </div>
                    <div className="text-right">
                      <div className="text-3xl font-bold text-green-400">#{player.ranking}</div>
                    </div>
                  </div>

                  {/* Player Name */}
                  <h3 className="text-xl font-bold text-white mb-1">
                    {player.firstName} {player.lastName}
                  </h3>
                  <p className="text-green-200 text-sm mb-4 flex items-center gap-1">
                    <span>{player.countryCode || player.country}</span>
                  </p>

                  {/* Stats */}
                  <div className="grid grid-cols-2 gap-3 pt-4 border-t border-white/10">
                    <div>
                      <p className="text-green-200 text-xs uppercase">Points</p>
                      <p className="text-white font-semibold">{player.points?.toLocaleString() || 0}</p>
                    </div>
                    <div>
                      <p className="text-green-200 text-xs uppercase">Price</p>
                      <p className="text-yellow-400 font-semibold">${player.price?.toFixed(2) || '0.00'}</p>
                    </div>
                  </div>

                  {/* Add to Roster Button */}
                  {leagueId && (
                    <div className="mt-4 pt-4 border-t border-white/10">
                      {myRosterPlayerIds.has(player.id) ? (
                        <div className="w-full py-2 bg-green-600/20 text-green-400 text-center rounded-lg text-sm font-medium">
                          ✓ On Your Roster
                        </div>
                      ) : budgetInfo && (player.price || 0) > budgetInfo.budget ? (
                        <div className="w-full py-2 bg-red-600/20 text-red-400 text-center rounded-lg text-sm">
                          Insufficient Budget
                        </div>
                      ) : budgetInfo && budgetInfo.currentRosterSize >= budgetInfo.maxRosterSize ? (
                        <div className="w-full py-2 bg-yellow-600/20 text-yellow-400 text-center rounded-lg text-sm">
                          Roster Full
                        </div>
                      ) : (
                        <button
                          onClick={() => addToRoster(player.id)}
                          disabled={addingPlayer === player.id}
                          className="w-full py-2 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white font-medium rounded-lg transition-all disabled:opacity-50"
                        >
                          {addingPlayer === player.id ? 'Adding...' : '+ Add to Roster'}
                        </button>
                      )}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </>
        )}
      </main>
    </div>
  );
}
