'use client';

import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/contexts/AuthContext';
import { leaguesApi, authApi, League, LeagueMember } from '@/lib/api';

export default function LeagueDetailPage() {
  const params = useParams();
  const router = useRouter();
  const { user, loading: authLoading } = useAuth();
  const leagueId = Number(params.id);

  const [league, setLeague] = useState<League | null>(null);
  const [members, setMembers] = useState<LeagueMember[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [backendUserId, setBackendUserId] = useState<number | null>(null);
  const [isMember, setIsMember] = useState(false);

  // Join Modal State
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [teamName, setTeamName] = useState('');
  const [joining, setJoining] = useState(false);

  useEffect(() => {
    const fetchBackendUser = async () => {
      if (user?.id) {
        try {
          const profile = await authApi.getProfile(user.id) as { id: number };
          setBackendUserId(profile.id);
        } catch (err) {
          console.log('User not found in backend, registering...', user.id);
          try {
            await authApi.register({
              supabaseId: user.id,
              email: user.email || '',
              displayName: user.displayName || 'New Player'
            });
            const profile = await authApi.getProfile(user.id) as { id: number };
            setBackendUserId(profile.id);
          } catch (regErr) {
            console.error('Failed to sync user profile', regErr);
          }
        }
      }
    };
    fetchBackendUser();
  }, [user]);

  useEffect(() => {
    if (!authLoading && !user) {
      router.push('/login');
    }
  }, [user, authLoading, router]);

  useEffect(() => {
    if (leagueId) {
      fetchLeagueData();
    }
  }, [leagueId, backendUserId]);

  const fetchLeagueData = async () => {
    setLoading(true);
    try {
      const [leagueData, membersData] = await Promise.all([
        leaguesApi.getById(leagueId),
        leaguesApi.getStandings(leagueId)
      ]);
      setLeague(leagueData);
      setMembers(membersData);

      // Check membership
      if (backendUserId) {
        const member = membersData.find(m => m.user?.id === backendUserId);
        setIsMember(!!member);
      }
    } catch (err: any) {
      console.error('Error fetching league:', err);
      setError('Failed to load league data: ' + (err.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  };

  const handleJoinLeague = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!backendUserId || !league) return;

    setJoining(true);
    try {
      await leaguesApi.join({
        joinCode: league.joinCode,
        userId: backendUserId,
        teamName: teamName
      });
      alert('Successfully joined the league!');
      setShowJoinModal(false);
      setTeamName('');
      fetchLeagueData();
    } catch (err: any) {
      console.error('Error joining league:', err);
      alert(err.message || 'Failed to join league');
    } finally {
      setJoining(false);
    }
  };

  const handleLeaveLeague = async () => {
    if (!user || !backendUserId) return;

    if (!confirm('Are you sure you want to leave this league? Your team and roster will be deleted.')) {
      return;
    }

    try {
      await leaguesApi.leave(leagueId, backendUserId);
      alert('Successfully left the league');
      router.push('/dashboard');
    } catch (err: any) {
      console.error('Error leaving league:', err);
      alert(err.message || 'Failed to leave league');
    }
  };

  if (authLoading || loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900 flex items-center justify-center">
        <div className="text-6xl animate-bounce">🎾</div>
      </div>
    );
  }

  if (error || !league) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900 flex items-center justify-center p-4">
        <div className="text-center">
          <div className="text-6xl mb-4">⚠️</div>
          <p className="text-red-300 text-xl mb-4">{error || 'League not found'}</p>
          <Link href="/dashboard" className="text-green-400 hover:text-green-300">← Back to Dashboard</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900">
      <header className="bg-black/30 backdrop-blur-lg border-b border-white/10">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-3xl">🎾</span>
            <Link href="/" className="text-2xl font-bold text-white">Tennis Fantasy</Link>
          </div>
          <nav className="flex gap-4">
            <Link href="/dashboard" className="text-green-200 hover:text-white transition-colors">Dashboard</Link>
            <Link href="/players" className="text-green-200 hover:text-white transition-colors">Players</Link>
            <Link href="/leaderboard" className="text-green-200 hover:text-white transition-colors">Leaderboard</Link>
          </nav>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        <div className="mb-8">
          <Link href="/dashboard" className="text-green-400 hover:text-green-300 text-sm mb-2 inline-block">← Back to Dashboard</Link>
          <h1 className="text-4xl font-bold text-white mb-2">{league.name}</h1>
          <p className="text-green-200">{league.description || 'Fantasy Tennis League'}</p>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-white/5 rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Teams</p>
            <p className="text-2xl font-bold text-white">{league.currentTeams}/{league.maxTeams}</p>
          </div>
          <div className="bg-white/5 rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Roster Size</p>
            <p className="text-2xl font-bold text-white">{league.rosterSize}</p>
          </div>
          <div className="bg-white/5 rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Tour</p>
            <p className="text-2xl font-bold text-white">{league.tourType}</p>
          </div>
          <div className="bg-white/5 rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Status</p>
            <p className="text-2xl font-bold text-green-400">{league.status}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <h2 className="text-2xl font-bold text-white mb-4">League Standings</h2>
            <div className="bg-white/5 rounded-xl border border-white/10 overflow-hidden">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-white/10">
                    <th className="text-left text-green-200 text-sm px-4 py-3">Rank</th>
                    <th className="text-left text-green-200 text-sm px-4 py-3">Team</th>
                    <th className="text-center text-green-200 text-sm px-4 py-3">W-L</th>
                    <th className="text-right text-green-200 text-sm px-4 py-3">Points</th>
                    <th className="text-right text-green-200 text-sm px-4 py-3">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {members.map((member, index) => (
                    <tr key={member.id} className="border-b border-white/5">
                      <td className="px-4 py-3 text-white font-bold">{index + 1}</td>
                      <td className="px-4 py-3 text-white">{member.teamName}</td>
                      <td className="px-4 py-3 text-center text-green-400">{member.wins} - {member.losses}</td>
                      <td className="px-4 py-3 text-right text-green-400 font-bold">{member.totalPoints}</td>
                      <td className="px-4 py-3 text-right">
                        <Link
                          href={`/leagues/${leagueId}/roster?teamId=${member.id}`}
                          className="text-green-400 hover:text-green-300 text-sm"
                        >
                          View Roster
                        </Link>
                      </td>
                    </tr>
                  ))}
                  {members.length === 0 && (
                    <tr>
                      <td colSpan={4} className="px-4 py-8 text-center text-green-200">No teams joined yet</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          <div>
            <h2 className="text-2xl font-bold text-white mb-4">Actions</h2>
            <div className="space-y-4">
              {isMember ? (
                <>
                  <Link
                    href={`/leagues/${leagueId}/roster`}
                    className="block w-full py-3 bg-gradient-to-r from-green-500 to-emerald-500 text-white font-bold rounded-lg text-center shadow-lg"
                  >
                    📋 Manage My Roster
                  </Link>
                  <Link
                    href={`/players?leagueId=${leagueId}`}
                    className="block w-full py-3 bg-white/10 hover:bg-white/20 text-white font-medium rounded-lg text-center transition-all border border-white/20"
                  >
                    👥 Browse Players
                  </Link>
                  <button
                    onClick={handleLeaveLeague}
                    className="w-full py-3 bg-red-600/10 hover:bg-red-600/20 text-red-400 font-medium rounded-lg transition-all border border-red-500/20"
                  >
                    🚪 Leave League
                  </button>
                </>
              ) : (
                <button
                  onClick={() => setShowJoinModal(true)}
                  className="w-full py-3 bg-gradient-to-r from-blue-500 to-indigo-500 text-white font-bold rounded-lg shadow-lg"
                >
                  🚀 Join League
                </button>
              )}

              <button
                className="w-full py-3 bg-white/10 text-white font-medium rounded-lg border border-white/20"
                onClick={() => {
                  navigator.clipboard.writeText(league.joinCode);
                  alert('Join code copied!');
                }}
              >
                📋 Copy Invite Code
              </button>
            </div>
          </div>
        </div>
      </main>

      {/* Join Modal */}
      {showJoinModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-slate-800 rounded-2xl p-8 w-full max-w-md border border-white/10">
            <h2 className="text-2xl font-bold text-white mb-6">Join {league.name}</h2>
            <form onSubmit={handleJoinLeague} className="space-y-4">
              <div>
                <label className="block text-green-100 text-sm font-medium mb-2">Team Name</label>
                <input
                  type="text"
                  value={teamName}
                  onChange={e => setTeamName(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg bg-white/10 border border-white/20 text-white focus:ring-2 focus:ring-green-400 outline-none"
                  placeholder="The Racqueteers"
                  required
                />
              </div>
              <div className="flex gap-4 mt-6">
                <button
                  type="button"
                  onClick={() => setShowJoinModal(false)}
                  className="flex-1 py-3 bg-white/10 text-white rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={joining}
                  className="flex-1 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg disabled:opacity-50"
                >
                  {joining ? 'Joining...' : 'Join Now'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
