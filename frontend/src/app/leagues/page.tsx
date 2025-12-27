'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { useAuth } from '@/contexts/AuthContext';
import { leaguesApi, authApi, League } from '@/lib/api';
import { useRouter } from 'next/navigation';

export default function LeaguesPage() {
    const { user, loading: authLoading } = useAuth();
    const router = useRouter();
    const [leagues, setLeagues] = useState<League[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Create League State
    const [showCreateLeague, setShowCreateLeague] = useState(false);
    const [leagueName, setLeagueName] = useState('');
    const [teamName, setTeamName] = useState('');
    const [creating, setCreating] = useState(false);
    const [backendUserId, setBackendUserId] = useState<number | null>(null);

    useEffect(() => {
        if (!authLoading && !user) {
            router.push('/login');
        }
    }, [user, authLoading, router]);

    useEffect(() => {
        const fetchBackendUser = async () => {
            if (user?.id) {
                try {
                    const profile: any = await authApi.getProfile(user.id);
                    setBackendUserId(profile.id);
                } catch (err: any) {
                    setError('Unable to load user profile. If you just logged in, please refresh. Error: ' + (err.message || 'Unknown'));
                    console.error('Failed to fetch user profile', err);
                }
            }
        };
        fetchBackendUser();
    }, [user]);

    const fetchLeagues = useCallback(async () => {
        if (authLoading || !user) return;

        setLoading(true);
        setError('');
        try {
            const data = await leaguesApi.getAll();
            setLeagues(data);
        } catch (err: any) {
            setError(err.message || 'Failed to load leagues');
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [authLoading, user]);

    useEffect(() => {
        fetchLeagues();
    }, [fetchLeagues]);

    const handleCreateLeague = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!leagueName || !teamName || !backendUserId) {
            // If user is logged in but backend ID is missing, we might need to handle that, 
            // but typically it should be loaded by now.
            if (!backendUserId) setError('User profile loading... please wait');
            else setError('Missing required info.');
            return;
        }
        setCreating(true);
        setError('');
        try {
            await leaguesApi.create({ name: leagueName, userId: backendUserId, teamName });
            setShowCreateLeague(false);
            setLeagueName('');
            setTeamName('');
            fetchLeagues(); // Refresh list
        } catch (err: any) {
            setError(err.message || 'Failed to create league');
        } finally {
            setCreating(false);
        }
    };

    if (authLoading) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-slate-900 via-green-900 to-slate-900 flex items-center justify-center">
                <div className="text-6xl animate-bounce">🎾</div>
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
                        <Link href="/leagues" className="text-green-200 text-white font-bold transition-colors">Leagues</Link>
                        <Link href="/players" className="text-green-200 hover:text-white transition-colors">Players</Link>
                        <Link href="/leaderboard" className="text-green-200 hover:text-white transition-colors">Leaderboard</Link>
                    </nav>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 py-8">
                <div className="flex justify-between items-center mb-8">
                    <div>
                        <h1 className="text-4xl font-bold text-white mb-2">Leagues</h1>
                        <p className="text-green-200">Join a league or start your own</p>
                    </div>
                    <button
                        onClick={() => setShowCreateLeague(true)}
                        className="px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white font-semibold rounded-lg transition-all shadow-lg"
                    >
                        + Create League
                    </button>
                </div>

                {error && !showCreateLeague && (
                    <div className="mb-6 p-4 bg-red-500/20 border border-red-500/50 rounded-xl flex items-center justify-between">
                        <div className="flex items-center gap-3 text-red-200">
                            <span>⚠️</span>
                            <p>{error}</p>
                        </div>
                        <button
                            onClick={() => fetchLeagues()}
                            className="text-sm bg-red-500/20 hover:bg-red-500/30 px-3 py-1 rounded-lg transition-colors text-white"
                        >
                            Retry
                        </button>
                    </div>
                )}

                {loading ? (
                    <div className="text-center py-16">
                        <div className="text-6xl mb-4 animate-bounce">🎾</div>
                        <p className="text-green-200 text-xl">Loading leagues...</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {/* Create Card */}
                        <button
                            onClick={() => setShowCreateLeague(true)}
                            className="bg-white/5 border-2 border-dashed border-white/20 rounded-xl p-8 flex flex-col items-center justify-center text-center hover:bg-white/10 hover:border-green-500/50 transition-all group"
                        >
                            <div className="text-4xl mb-4 group-hover:scale-110 transition-transform">🏆</div>
                            <h3 className="text-xl font-bold text-white mb-2">Create New League</h3>
                            <p className="text-green-200 text-sm">Start a fresh league and invite your friends</p>
                        </button>

                        {leagues.map((league) => (
                            <div
                                key={league.id}
                                className="bg-white/5 backdrop-blur-lg rounded-xl p-6 border border-white/10 hover:border-green-500/50 transition-all"
                            >
                                <div className="flex justify-between items-start mb-4">
                                    <div>
                                        <h3 className="text-xl font-bold text-white mb-1">{league.name}</h3>
                                        <span className="px-2 py-0.5 rounded text-xs font-bold bg-blue-500/20 text-blue-300">
                                            {league.tourType || 'ATP/WTA'}
                                        </span>
                                    </div>
                                    <div>
                                        <span className={`px-2 py-1 rounded text-xs font-bold ${league.status === 'ACTIVE' ? 'bg-green-500/20 text-green-300' : 'bg-yellow-500/20 text-yellow-300'}`}>
                                            {league.status || 'Active'}
                                        </span>
                                    </div>
                                </div>

                                <div className="grid grid-cols-2 gap-4 mb-4 text-sm">
                                    <div>
                                        <p className="text-green-200">Teams</p>
                                        <p className="text-white font-bold">{league.currentTeams || 0} / {league.maxTeams || 8}</p>
                                    </div>
                                    <div>
                                        <p className="text-green-200">Roster</p>
                                        <p className="text-white font-bold">{league.rosterSize || 6} Players</p>
                                    </div>
                                </div>

                                <Link
                                    href={`/leagues/${league.id}`}
                                    className="block w-full py-2 bg-white/10 hover:bg-white/20 text-white text-center rounded-lg transition-colors font-medium"
                                >
                                    View Details
                                </Link>
                            </div>
                        ))}
                    </div>
                )}

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
