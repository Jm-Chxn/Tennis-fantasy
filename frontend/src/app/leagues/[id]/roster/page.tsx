'use client';

import { useState, useEffect, useCallback } from 'react';
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/contexts/AuthContext';
import { leaguesApi, rosterApi, authApi, League, Player } from '@/lib/api';

interface RosterPlayer {
  id: number;
  player: Player;
  isStarter: boolean;
  slotNumber: number;
  pointsEarned: number;
  acquisitionType: string;
}

interface BudgetInfo {
  budget: number;
  currentRosterSize: number;
  maxRosterSize: number;
  teamName: string;
}

export default function RosterPage() {
  const params = useParams();
  const router = useRouter();
  const searchParams = useSearchParams();
  const { user, loading: authLoading } = useAuth();
  const leagueId = Number(params.id);
  const viewingTeamId = searchParams.get('teamId');

  const [league, setLeague] = useState<League | null>(null);
  const [roster, setRoster] = useState<RosterPlayer[]>([]);
  const [budgetInfo, setBudgetInfo] = useState<BudgetInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState<number | null>(null);

  const [backendUserId, setBackendUserId] = useState<number | null>(null);

  useEffect(() => {
    const fetchBackendUser = async () => {
      if (user?.id) {
        try {
          const profile = await authApi.getProfile(user.id) as { id: number };
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

  useEffect(() => {
    if (!authLoading && !user) {
      router.push('/login');
    }
  }, [user, authLoading, router]);

  const fetchData = useCallback(async () => {
    if (!userId) return;
    setLoading(true);
    try {
      const [leagueData, rosterData, budgetData] = await Promise.all([
        leaguesApi.getById(leagueId),
        rosterApi.get(leagueId, userId),
        rosterApi.getBudget(leagueId, userId)
      ]);
      setLeague(leagueData);
      setRoster(rosterData as RosterPlayer[]);
      setBudgetInfo(budgetData as BudgetInfo);
    } catch (err) {
      console.error('Error fetching roster:', err);
      setError('Failed to load roster data');
    } finally {
      setLoading(false);
    }
  }, [leagueId, userId]);

  useEffect(() => {
    if (leagueId && user) {
      fetchData();
    }
  }, [leagueId, user, fetchData]);

  const removePlayer = async (playerId: number) => {
    if (!userId) {
      alert('User profile not loaded');
      return;
    }
    setActionLoading(playerId);
    try {
      await rosterApi.remove(leagueId, userId, playerId);
      await fetchData();
    } catch (err) {
      console.error('Error removing player:', err);
      alert('Failed to remove player');
    } finally {
      setActionLoading(null);
    }
  };

  const toggleStarter = async (playerId: number, currentIsStarter: boolean) => {
    if (!userId) {
      alert('User profile not loaded');
      return;
    }
    setActionLoading(playerId);
    try {
      const starters = roster.filter(r => r.isStarter).map(r => r.player.id);
      const bench = roster.filter(r => !r.isStarter).map(r => r.player.id);

      if (currentIsStarter) {
        // Move to bench
        await rosterApi.updateLineup(
          leagueId,
          userId,
          starters.filter(id => id !== playerId),
          [...bench, playerId]
        );
      } else {
        // Move to starters
        await rosterApi.updateLineup(
          leagueId,
          userId,
          [...starters, playerId],
          bench.filter(id => id !== playerId)
        );
      }
      await fetchData();
    } catch (err) {
      console.error('Error updating lineup:', err);
      alert('Failed to update lineup');
    } finally {
      setActionLoading(null);
    }
  };

  if (authLoading || loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900 flex items-center justify-center">
        <div className="text-6xl animate-bounce">🎾</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900">
        <header className="bg-black/30 backdrop-blur-lg border-b border-white/10">
          <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <span className="text-3xl">🎾</span>
              <Link href="/" className="text-2xl font-bold text-white">Tennis Fantasy</Link>
            </div>
          </div>
        </header>
        <main className="max-w-7xl mx-auto px-4 py-8">
          <div className="text-center py-16">
            <div className="text-6xl mb-4">⚠️</div>
            <p className="text-red-300 text-xl mb-4">{error}</p>
            <Link href="/dashboard" className="text-green-400 hover:text-green-300">
              ← Back to Dashboard
            </Link>
          </div>
        </main>
      </div>
    );
  }

  const starters = roster.filter(r => r.isStarter);
  const bench = roster.filter(r => !r.isStarter);

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
            <Link href="/players" className="text-green-200 hover:text-white transition-colors">Players</Link>
            <Link href="/leaderboard" className="text-green-200 hover:text-white transition-colors">Leaderboard</Link>
          </nav>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        {/* Page Header */}
        <div className="mb-8">
          <Link href={`/leagues/${leagueId}`} className="text-green-400 hover:text-green-300 text-sm mb-2 inline-block">
            ← Back to {league?.name || 'League'}
          </Link>
          <h1 className="text-4xl font-bold text-white mb-2">
            {budgetInfo?.teamName || 'My Roster'}
          </h1>
          <p className="text-green-200">Manage your fantasy tennis team</p>
        </div>

        {/* Budget & Stats Bar */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-gradient-to-br from-yellow-600 to-amber-600 rounded-xl p-4 text-white">
            <p className="text-yellow-100 text-sm flex items-center gap-1">
              💰 Budget
            </p>
            <p className="text-3xl font-bold">${budgetInfo?.budget?.toFixed(2) || '100.00'}</p>
          </div>
          <div className="bg-white/5 backdrop-blur-lg rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Roster Size</p>
            <p className="text-2xl font-bold text-white">
              {budgetInfo?.currentRosterSize || 0}/{budgetInfo?.maxRosterSize || 6}
            </p>
          </div>
          <div className="bg-white/5 backdrop-blur-lg rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Starters</p>
            <p className="text-2xl font-bold text-green-400">{starters.length}</p>
          </div>
          <div className="bg-white/5 backdrop-blur-lg rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Bench</p>
            <p className="text-2xl font-bold text-yellow-400">{bench.length}</p>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="flex gap-4 mb-8">
          <Link
            href={`/players?leagueId=${leagueId}`}
            className="px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white font-semibold rounded-lg transition-all shadow-lg"
          >
            + Add Players
          </Link>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Starting Lineup */}
          <div>
            <h2 className="text-2xl font-bold text-white mb-4 flex items-center gap-2">
              ⭐ Starting Lineup
              <span className="text-sm font-normal text-green-200">({starters.length})</span>
            </h2>
            <div className="space-y-3">
              {starters.length > 0 ? (
                starters.map((rosterEntry) => (
                  <div
                    key={rosterEntry.id}
                    className="bg-white/5 backdrop-blur-lg rounded-xl p-4 border border-green-500/30 hover:border-green-500/50 transition-all"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-emerald-600 rounded-full flex items-center justify-center text-white font-bold">
                          #{rosterEntry.player.ranking}
                        </div>
                        <div>
                          <h3 className="text-white font-bold">
                            {rosterEntry.player.firstName} {rosterEntry.player.lastName}
                          </h3>
                          <div className="flex items-center gap-2 text-sm">
                            <span className={`px-2 py-0.5 rounded ${rosterEntry.player.tour === 'ATP'
                              ? 'bg-blue-500/20 text-blue-300'
                              : 'bg-pink-500/20 text-pink-300'
                              }`}>
                              {rosterEntry.player.tour}
                            </span>
                            <span className="text-green-200">{rosterEntry.player.country}</span>
                          </div>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="text-right mr-4">
                          <p className="text-green-400 font-bold">{rosterEntry.pointsEarned || 0} pts</p>
                          <p className="text-yellow-400 text-sm">${rosterEntry.player.price?.toFixed(2)}</p>
                        </div>
                        <button
                          onClick={() => toggleStarter(rosterEntry.player.id, true)}
                          disabled={actionLoading === rosterEntry.player.id}
                          className="px-3 py-1 text-xs bg-yellow-600/20 text-yellow-400 hover:bg-yellow-600/40 rounded transition-colors disabled:opacity-50"
                        >
                          {actionLoading === rosterEntry.player.id ? '...' : 'Bench'}
                        </button>
                        <button
                          onClick={() => removePlayer(rosterEntry.player.id)}
                          disabled={actionLoading === rosterEntry.player.id}
                          className="px-3 py-1 text-xs bg-red-600/20 text-red-400 hover:bg-red-600/40 rounded transition-colors disabled:opacity-50"
                        >
                          {actionLoading === rosterEntry.player.id ? '...' : 'Drop'}
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="bg-white/5 rounded-xl p-8 text-center border border-dashed border-white/20">
                  <p className="text-green-200 mb-4">No starters yet</p>
                  <Link
                    href={`/players?leagueId=${leagueId}`}
                    className="text-green-400 hover:text-green-300"
                  >
                    Add players to your roster →
                  </Link>
                </div>
              )}
            </div>
          </div>

          {/* Bench */}
          <div>
            <h2 className="text-2xl font-bold text-white mb-4 flex items-center gap-2">
              🪑 Bench
              <span className="text-sm font-normal text-green-200">({bench.length})</span>
            </h2>
            <div className="space-y-3">
              {bench.length > 0 ? (
                bench.map((rosterEntry) => (
                  <div
                    key={rosterEntry.id}
                    className="bg-white/5 backdrop-blur-lg rounded-xl p-4 border border-white/10 hover:border-white/20 transition-all opacity-80"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 bg-white/10 rounded-full flex items-center justify-center text-white font-bold">
                          #{rosterEntry.player.ranking}
                        </div>
                        <div>
                          <h3 className="text-white font-bold">
                            {rosterEntry.player.firstName} {rosterEntry.player.lastName}
                          </h3>
                          <div className="flex items-center gap-2 text-sm">
                            <span className={`px-2 py-0.5 rounded ${rosterEntry.player.tour === 'ATP'
                              ? 'bg-blue-500/20 text-blue-300'
                              : 'bg-pink-500/20 text-pink-300'
                              }`}>
                              {rosterEntry.player.tour}
                            </span>
                            <span className="text-green-200">{rosterEntry.player.country}</span>
                          </div>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="text-right mr-4">
                          <p className="text-gray-400 font-bold">{rosterEntry.pointsEarned || 0} pts</p>
                          <p className="text-yellow-400 text-sm">${rosterEntry.player.price?.toFixed(2)}</p>
                        </div>
                        <button
                          onClick={() => toggleStarter(rosterEntry.player.id, false)}
                          disabled={actionLoading === rosterEntry.player.id}
                          className="px-3 py-1 text-xs bg-green-600/20 text-green-400 hover:bg-green-600/40 rounded transition-colors disabled:opacity-50"
                        >
                          {actionLoading === rosterEntry.player.id ? '...' : 'Start'}
                        </button>
                        <button
                          onClick={() => removePlayer(rosterEntry.player.id)}
                          disabled={actionLoading === rosterEntry.player.id}
                          className="px-3 py-1 text-xs bg-red-600/20 text-red-400 hover:bg-red-600/40 rounded transition-colors disabled:opacity-50"
                        >
                          {actionLoading === rosterEntry.player.id ? '...' : 'Drop'}
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="bg-white/5 rounded-xl p-8 text-center border border-dashed border-white/20">
                  <p className="text-green-200">No benched players</p>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Empty Roster Message */}
        {roster.length === 0 && (
          <div className="mt-8 bg-white/5 rounded-2xl p-12 text-center border border-dashed border-white/20">
            <div className="text-6xl mb-4">📋</div>
            <h2 className="text-2xl font-bold text-white mb-2">Your Roster is Empty</h2>
            <p className="text-green-200 mb-6">Start building your team by adding players</p>
            <Link
              href={`/players?leagueId=${leagueId}`}
              className="inline-block px-8 py-3 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white font-semibold rounded-lg transition-all shadow-lg"
            >
              Browse Available Players
            </Link>
          </div>
        )}
      </main>
    </div>
  );
}
