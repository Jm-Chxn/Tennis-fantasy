'use client';

import { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { leaguesApi } from '@/lib/api';

export default function DashboardPage() {
  const { user, signOut, loading } = useAuth();
  const router = useRouter();
  const [showCreateLeague, setShowCreateLeague] = useState(false);
  const [leagues, setLeagues] = useState<any[]>([]);
  const [leagueName, setLeagueName] = useState('');
  const [teamName, setTeamName] = useState('');
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState('');

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!loading && !user) {
      router.push('/login');
    }
  }, [user, loading, router]);

  // Fetch user's leagues
  useEffect(() => {
    const fetchLeagues = async () => {
      if (!user) return;
      try {
        // For demo, fetch all leagues (should filter by user in real app)
        const allLeagues = await leaguesApi.getAll();
        setLeagues(allLeagues);
      } catch (err) {
        setError('Failed to load leagues');
      }
    };
    fetchLeagues();
  }, [user, creating]);

  const handleCreateLeague = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!leagueName || !teamName) return;
    setCreating(true);
    setError('');
    try {
      // Use mock userId for now
      const userId = user?.id || 1;
      await leaguesApi.create({ name: leagueName, userId, teamName });
      setShowCreateLeague(false);
      setLeagueName('');
      setTeamName('');
    } catch (err: any) {
      setError(err.message || 'Failed to create league');
    } finally {
      setCreating(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900 flex items-center justify-center">
        <div className="text-6xl animate-bounce">🎾</div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900">
      {/* Header */}
      <header className="bg-black/30 backdrop-blur-lg border-b border-white/10">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-3xl">🎾</span>
            <Link href="/" className="text-2xl font-bold text-white">Tennis Fantasy</Link>
          </div>
          <div className="flex items-center gap-6">
            <nav className="flex gap-4">
              <Link href="/players" className="text-green-200 hover:text-white transition-colors">Players</Link>
              <Link href="/leaderboard" className="text-green-200 hover:text-white transition-colors">Leaderboard</Link>
            </nav>
            <div className="flex items-center gap-3">
              <span className="text-green-200">{user.displayName}</span>
              <button
                onClick={signOut}
                className="px-3 py-1 text-sm bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors"
              >
                Sign Out
              </button>
            </div>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        {/* Welcome */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-white mb-2">Welcome back, {user.displayName}! 👋</h1>
          <p className="text-green-200">Here&apos;s your fantasy tennis overview</p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-gradient-to-br from-green-600 to-green-700 rounded-xl p-6 text-white">
            <p className="text-green-100 text-sm mb-1">Total Points</p>
            <p className="text-3xl font-bold">254</p>
          </div>
          <div className="bg-gradient-to-br from-blue-600 to-blue-700 rounded-xl p-6 text-white">
            <p className="text-blue-100 text-sm mb-1">Active Leagues</p>
            <p className="text-3xl font-bold">2</p>
          </div>
          <div className="bg-gradient-to-br from-purple-600 to-purple-700 rounded-xl p-6 text-white">
            <p className="text-purple-100 text-sm mb-1">Best Rank</p>
            <p className="text-3xl font-bold">#2</p>
          </div>
          <div className="bg-gradient-to-br from-yellow-600 to-yellow-700 rounded-xl p-6 text-white">
            <p className="text-yellow-100 text-sm mb-1">Players Drafted</p>
            <p className="text-3xl font-bold">12</p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* My Leagues */}
          <div className="lg:col-span-2">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-2xl font-bold text-white">My Leagues</h2>
              <button
                onClick={() => setShowCreateLeague(true)}
                className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
              >
                + Create League
              </button>
            </div>

            <div className="space-y-4">
              {leagues.length > 0 ? leagues.map((league) => (
                <div
                  key={league.id}
                  className="bg-white/5 backdrop-blur-lg rounded-xl p-6 border border-white/10 hover:border-green-500/50 transition-all"
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <h3 className="text-xl font-bold text-white mb-1">{league.name}</h3>
                      <p className="text-green-200 text-sm">{league.currentTeams || 1}/{league.maxTeams} teams</p>
                    </div>
                  </div>
                  <div className="mt-4 flex gap-2">
                    <Link
                      href={`/leagues/${league.id}`}
                      className="px-4 py-2 bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors text-sm"
                    >
                      View League
                    </Link>
                    <Link
                      href={`/leagues/${league.id}/roster`}
                      className="px-4 py-2 bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors text-sm"
                    >
                      My Roster
                    </Link>
                  </div>
                </div>
              )) : (
                <div className="bg-white/5 rounded-xl p-8 text-center border border-dashed border-white/20">
                  <p className="text-green-200 mb-4">You haven&apos;t joined any leagues yet</p>
                  <button className="px-6 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors">
                    Browse Public Leagues
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* Recent Activity */}
          <div>
            <h2 className="text-2xl font-bold text-white mb-4">Recent Activity</h2>
            <div className="bg-white/5 backdrop-blur-lg rounded-xl border border-white/10 overflow-hidden">
              {mockRecentActivity.map((activity, index) => (
                <div
                  key={activity.id}
                  className={`p-4 ${index !== mockRecentActivity.length - 1 ? 'border-b border-white/10' : ''}`}
                >
                  <p className="text-white text-sm">{activity.message}</p>
                  <p className="text-green-300 text-xs mt-1">{activity.time}</p>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="mt-8">
          <h2 className="text-2xl font-bold text-white mb-4">Quick Actions</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Link
              href="/players"
              className="bg-white/5 hover:bg-white/10 rounded-xl p-6 text-center border border-white/10 transition-all"
            >
              <div className="text-3xl mb-2">👥</div>
              <p className="text-white font-medium">Browse Players</p>
            </Link>
            <button
              onClick={() => setShowCreateLeague(true)}
              className="bg-white/5 hover:bg-white/10 rounded-xl p-6 text-center border border-white/10 transition-all"
            >
              <div className="text-3xl mb-2">➕</div>
              <p className="text-white font-medium">Create League</p>
            </button>
            <Link
              href="/leaderboard"
              className="bg-white/5 hover:bg-white/10 rounded-xl p-6 text-center border border-white/10 transition-all"
            >
              <div className="text-3xl mb-2">🏆</div>
              <p className="text-white font-medium">Leaderboard</p>
            </Link>
            <Link
              href="/profile"
              className="bg-white/5 hover:bg-white/10 rounded-xl p-6 text-center border border-white/10 transition-all"
            >
              <div className="text-3xl mb-2">⚙️</div>
              <p className="text-white font-medium">Settings</p>
            </Link>
          </div>
        </div>
      </main>

      {/* Create League Modal */}
      {showCreateLeague && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-slate-800 rounded-2xl p-8 w-full max-w-md border border-white/10">
            <h2 className="text-2xl font-bold text-white mb-6">Create New League</h2>
            <form className="space-y-4" onSubmit={handleCreateLeague}>
              <div>
                <label className="block text-green-100 text-sm font-medium mb-2">League Name</label>
                <input
                  type="text"
                  value={leagueName}
                  onChange={e => setLeagueName(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg bg-white/10 border border-white/20 text-white focus:outline-none focus:ring-2 focus:ring-green-400"
                  placeholder="My Awesome League"
                  required
                />
              </div>
              <div>
                <label className="block text-green-100 text-sm font-medium mb-2">Your Team Name</label>
                <input
                  type="text"
                  value={teamName}
                  onChange={e => setTeamName(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg bg-white/10 border border-white/20 text-white focus:outline-none focus:ring-2 focus:ring-green-400"
                  placeholder="The Aces"
                  required
                />
              </div>
              {error && <div className="text-red-400 text-sm">{error}</div>}
              <div className="flex gap-4 mt-6">
                <button
                  type="button"
                  onClick={() => setShowCreateLeague(false)}
                  className="flex-1 py-3 bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
                  disabled={creating}
                >
                  {creating ? 'Creating...' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
