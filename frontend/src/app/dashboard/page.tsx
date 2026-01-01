'use client';

import { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { leaguesApi, authApi } from '@/lib/api';

export default function DashboardPage() {
  const { user, signOut, loading } = useAuth();
  const router = useRouter();
  const [showCreateLeague, setShowCreateLeague] = useState(false);
  const [showJoinLeague, setShowJoinLeague] = useState(false);
  const [leagues, setLeagues] = useState<any[]>([]);
  const [leaguesLoading, setLeaguesLoading] = useState(true);
  const [leagueName, setLeagueName] = useState('');
  const [teamName, setTeamName] = useState('');
  const [joinCode, setJoinCode] = useState('');
  const [joinTeamName, setJoinTeamName] = useState('');
  const [creating, setCreating] = useState(false);
  const [joining, setJoining] = useState(false);
  const [error, setError] = useState('');
  const [backendUserId, setBackendUserId] = useState<number | null>(null);

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!loading && !user) {
      router.push('/login');
    }
  }, [user, loading, router]);

  // Fetch user's backend profile and leagues
  useEffect(() => {
    const fetchProfileAndLeagues = async () => {
      setLeaguesLoading(true);
      try {
        if (!user) return;

        let profile;
        try {
          profile = await authApi.getProfile(user.id) as { id: number };
        } catch (err: any) {
          // If user doesn't exist in backend, register them now
          console.log('User not found in backend, registering...', user.id);
          await authApi.register({
            supabaseId: user.id,
            email: user.email || '',
            displayName: user.displayName || 'New Player'
          });
          profile = await authApi.getProfile(user.id) as { id: number };
        }

        setBackendUserId(profile.id);
        const joinedLeagues = await leaguesApi.getJoinedLeagues(profile.id);
        setLeagues(joinedLeagues);
      } catch (err) {
        console.error('Dashboard load error:', err);
        setError('Failed to load leagues or user profile');
        setLeagues([]);
      } finally {
        setLeaguesLoading(false);
      }
    };
    fetchProfileAndLeagues();
  }, [user, creating, joining]);

  const handleCreateLeague = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!leagueName || !teamName || !backendUserId) {
      setError('Missing required info.');
      return;
    }
    setCreating(true);
    setError('');
    try {
      await leaguesApi.create({ name: leagueName, userId: backendUserId, teamName });
      setShowCreateLeague(false);
      setLeagueName('');
      setTeamName('');
    } catch (err: any) {
      setError(err.message || 'Failed to create league');
    } finally {
      setCreating(false);
    }
  };

  const handleJoinLeague = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!joinCode || !joinTeamName || !backendUserId) {
      setError('Missing required info.');
      return;
    }
    setJoining(true);
    setError('');
    try {
      await leaguesApi.join({ joinCode, userId: backendUserId, teamName: joinTeamName });
      setShowJoinLeague(false);
      setJoinCode('');
      setJoinTeamName('');
    } catch (err: any) {
      setError(err.message || 'Failed to join league');
    } finally {
      setJoining(false);
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
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900 flex items-center justify-center">
        <div className="text-white text-xl">Not signed in</div>
      </div>
    );
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
              <Link href="/leagues" className="text-green-200 hover:text-white transition-colors">Leagues</Link>
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
              <div className="flex gap-2">
                <button
                  onClick={() => setShowJoinLeague(true)}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
                >
                  🔗 Join League
                </button>
                <button
                  onClick={() => setShowCreateLeague(true)}
                  className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
                >
                  + Create League
                </button>
              </div>
            </div>

            <div className="space-y-4">
              {leaguesLoading ? (
                <div className="bg-white/5 rounded-xl p-8 text-center border border-white/10">
                  <div className="text-4xl mb-2 animate-bounce">🎾</div>
                  <p className="text-green-200">Loading your leagues...</p>
                </div>
              ) : leagues.length > 0 ? leagues.map((league) => (
                <div
                  key={league.id}
                  className="bg-white/5 backdrop-blur-lg rounded-xl p-6 border border-white/10 hover:border-green-500/50 transition-all"
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <h3 className="text-xl font-bold text-white mb-1">{league.name}</h3>
                      <div className="flex items-center gap-2 text-sm">
                        <span className="text-green-200">{league.currentTeams || 1}/{league.maxTeams} teams</span>
                        <span className="px-2 py-0.5 rounded bg-blue-500/20 text-blue-300 text-xs">
                          {league.tourType || 'ATP/WTA'}
                        </span>
                        <span className={`px-2 py-0.5 rounded text-xs ${league.status === 'ACTIVE' ? 'bg-green-500/20 text-green-300' : 'bg-yellow-500/20 text-yellow-300'}`}>
                          {league.status || 'ACTIVE'}
                        </span>
                      </div>
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
                  <div className="flex justify-center gap-4">
                    <button
                      onClick={() => setShowJoinLeague(true)}
                      className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
                    >
                      Join with Code
                    </button>
                    <Link
                      href="/leagues"
                      className="inline-block px-6 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
                    >
                      Browse Public Leagues
                    </Link>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Recent Activity */}
          <div>
            <h2 className="text-2xl font-bold text-white mb-4">Recent Activity</h2>
            <div className="bg-white/5 backdrop-blur-lg rounded-xl border border-white/10 overflow-hidden p-8 text-center text-green-200">
              No recent activity yet.
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

      {/* Join League Modal */}
      {showJoinLeague && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-slate-800 rounded-2xl p-8 w-full max-w-md border border-white/10">
            <h2 className="text-2xl font-bold text-white mb-6">Join a League</h2>
            <form className="space-y-4" onSubmit={handleJoinLeague}>
              <div>
                <label className="block text-green-100 text-sm font-medium mb-2">Invite Code</label>
                <input
                  type="text"
                  value={joinCode}
                  onChange={e => setJoinCode(e.target.value.toUpperCase())}
                  className="w-full px-4 py-3 rounded-lg bg-white/10 border border-white/20 text-white focus:outline-none focus:ring-2 focus:ring-blue-400 uppercase tracking-widest text-center text-lg font-mono"
                  placeholder="ABC123"
                  maxLength={6}
                  required
                />
                <p className="text-green-200/60 text-xs mt-1">Enter the 6-character code shared by the league owner</p>
              </div>
              <div>
                <label className="block text-green-100 text-sm font-medium mb-2">Your Team Name</label>
                <input
                  type="text"
                  value={joinTeamName}
                  onChange={e => setJoinTeamName(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg bg-white/10 border border-white/20 text-white focus:outline-none focus:ring-2 focus:ring-blue-400"
                  placeholder="The Aces"
                  required
                />
              </div>
              {error && <div className="text-red-400 text-sm">{error}</div>}
              <div className="flex gap-4 mt-6">
                <button
                  type="button"
                  onClick={() => { setShowJoinLeague(false); setError(''); }}
                  className="flex-1 py-3 bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
                  disabled={joining}
                >
                  {joining ? 'Joining...' : 'Join League'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
