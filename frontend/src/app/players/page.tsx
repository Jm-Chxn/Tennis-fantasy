'use client';

import { useState, useEffect } from 'react';
import { playersApi, Player } from '@/lib/api';
import Link from 'next/link';

export default function PlayersPage() {
  const [players, setPlayers] = useState<Player[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTour, setSelectedTour] = useState<'all' | 'ATP' | 'WTA'>('all');
  const [initialized, setInitialized] = useState(false);

  // Fetch players on mount
  useEffect(() => {
    fetchPlayers();
  }, []);

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
            <Link href="/leaderboard" className="text-green-200 hover:text-white transition-colors">Leaderboard</Link>
          </nav>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
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
                </div>
              ))}
            </div>
          </>
        )}
      </main>
    </div>
  );
}
