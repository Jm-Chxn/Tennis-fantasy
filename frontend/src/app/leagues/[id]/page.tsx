'use client';

import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/contexts/AuthContext';
import { leaguesApi, League, LeagueMember } from '@/lib/api';

export default function LeagueDetailPage() {
  const params = useParams();
  const router = useRouter();
  const { user, loading: authLoading } = useAuth();
  const leagueId = Number(params.id);

  const [league, setLeague] = useState<League | null>(null);
  const [members, setMembers] = useState<LeagueMember[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!authLoading && !user) {
      router.push('/login');
    }
  }, [user, authLoading, router]);

  useEffect(() => {
    if (leagueId) {
      fetchLeagueData();
    }
  }, [leagueId]);

  const fetchLeagueData = async () => {
    setLoading(true);
    try {
      const [leagueData, membersData] = await Promise.all([
        leaguesApi.getById(leagueId),
        leaguesApi.getStandings(leagueId)
      ]);
      setLeague(leagueData);
      setMembers(membersData);
    } catch (err) {
      console.error('Error fetching league:', err);
      setError('Failed to load league data');
    } finally {
      setLoading(false);
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
            <p className="text-red-300 text-xl mb-4">{error || 'League not found'}</p>
            <Link href="/dashboard" className="text-green-400 hover:text-green-300">
              ← Back to Dashboard
            </Link>
          </div>
        </main>
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
          <nav className="flex gap-4">
            <Link href="/dashboard" className="text-green-200 hover:text-white transition-colors">Dashboard</Link>
            <Link href="/players" className="text-green-200 hover:text-white transition-colors">Players</Link>
            <Link href="/leaderboard" className="text-green-200 hover:text-white transition-colors">Leaderboard</Link>
          </nav>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        {/* League Header */}
        <div className="mb-8">
          <Link href="/dashboard" className="text-green-400 hover:text-green-300 text-sm mb-2 inline-block">
            ← Back to Dashboard
          </Link>
          <h1 className="text-4xl font-bold text-white mb-2">{league.name}</h1>
          <p className="text-green-200">{league.description || 'Fantasy Tennis League'}</p>
        </div>

        {/* League Stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-white/5 backdrop-blur-lg rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Teams</p>
            <p className="text-2xl font-bold text-white">{league.currentTeams || members.length}/{league.maxTeams}</p>
          </div>
          <div className="bg-white/5 backdrop-blur-lg rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Roster Size</p>
            <p className="text-2xl font-bold text-white">{league.rosterSize || 6} players</p>
          </div>
          <div className="bg-white/5 backdrop-blur-lg rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Tour</p>
            <p className="text-2xl font-bold text-white">{league.tourType || 'All'}</p>
          </div>
          <div className="bg-white/5 backdrop-blur-lg rounded-xl p-4 border border-white/10">
            <p className="text-green-200 text-sm">Status</p>
            <p className={`text-2xl font-bold ${league.status === 'ACTIVE' ? 'text-green-400' : 'text-yellow-400'}`}>
              {league.status || 'Active'}
            </p>
          </div>
        </div>

        {/* Join Code */}
        {league.joinCode && (
          <div className="bg-gradient-to-r from-green-600/20 to-emerald-600/20 rounded-xl p-4 border border-green-500/30 mb-8">
            <p className="text-green-200 text-sm mb-1">Invite Code</p>
            <p className="text-2xl font-mono font-bold text-white tracking-wider">{league.joinCode}</p>
            <p className="text-green-300 text-xs mt-1">Share this code with friends to invite them</p>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Standings */}
          <div className="lg:col-span-2">
            <h2 className="text-2xl font-bold text-white mb-4">League Standings</h2>
            <div className="bg-white/5 backdrop-blur-lg rounded-xl border border-white/10 overflow-hidden">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-white/10">
                    <th className="text-left text-green-200 text-sm font-medium px-4 py-3">Rank</th>
                    <th className="text-left text-green-200 text-sm font-medium px-4 py-3">Team</th>
                    <th className="text-center text-green-200 text-sm font-medium px-4 py-3">W-L</th>
                    <th className="text-right text-green-200 text-sm font-medium px-4 py-3">Points</th>
                    <th className="text-right text-green-200 text-sm font-medium px-4 py-3">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {members.length > 0 ? (
                    members.map((member, index) => (
                      <tr 
                        key={member.id} 
                        className={`border-b border-white/5 ${index === 0 ? 'bg-yellow-500/10' : ''}`}
                      >
                        <td className="px-4 py-3">
                          <span className={`inline-flex items-center justify-center w-8 h-8 rounded-full font-bold ${
                            index === 0 ? 'bg-yellow-500 text-black' :
                            index === 1 ? 'bg-gray-400 text-black' :
                            index === 2 ? 'bg-amber-600 text-black' :
                            'bg-white/10 text-white'
                          }`}>
                            {member.leagueRank || index + 1}
                          </span>
                        </td>
                        <td className="px-4 py-3">
                          <p className="text-white font-medium">{member.teamName}</p>
                        </td>
                        <td className="px-4 py-3 text-center">
                          <span className="text-green-400">{member.wins || 0}</span>
                          <span className="text-white/50"> - </span>
                          <span className="text-red-400">{member.losses || 0}</span>
                        </td>
                        <td className="px-4 py-3 text-right">
                          <span className="text-xl font-bold text-green-400">{member.totalPoints || 0}</span>
                        </td>
                        <td className="px-4 py-3 text-right">
                          <Link
                            href={`/leagues/${leagueId}/roster?teamId=${member.id}`}
                            className="text-green-400 hover:text-green-300 text-sm"
                          >
                            View Roster
                          </Link>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={5} className="px-4 py-8 text-center text-green-200">
                        No teams have joined this league yet
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Actions & Info */}
          <div>
            <h2 className="text-2xl font-bold text-white mb-4">Actions</h2>
            <div className="space-y-4">
              <Link
                href={`/leagues/${leagueId}/roster`}
                className="block w-full py-3 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white font-semibold rounded-lg text-center transition-all shadow-lg"
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
                className="w-full py-3 bg-white/10 hover:bg-white/20 text-white font-medium rounded-lg transition-all border border-white/20"
                onClick={() => {
                  navigator.clipboard.writeText(league.joinCode || '');
                  alert('Join code copied to clipboard!');
                }}
              >
                📋 Copy Join Code
              </button>
            </div>

            {/* League Info */}
            <div className="mt-8">
              <h3 className="text-lg font-bold text-white mb-3">League Settings</h3>
              <div className="bg-white/5 rounded-xl p-4 border border-white/10 space-y-2">
                <div className="flex justify-between">
                  <span className="text-green-200 text-sm">Draft Type</span>
                  <span className="text-white text-sm">{league.draftType || 'Snake'}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-green-200 text-sm">Draft Status</span>
                  <span className={`text-sm ${league.draftStatus === 'COMPLETED' ? 'text-green-400' : 'text-yellow-400'}`}>
                    {league.draftStatus || 'Pending'}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
