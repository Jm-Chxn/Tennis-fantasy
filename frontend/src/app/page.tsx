import Link from "next/link";

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-green-900 via-green-800 to-emerald-900">
      {/* Hero Section */}
      <div className="relative overflow-hidden">
        {/* Background decoration */}
        <div className="absolute inset-0 opacity-10">
          <div className="absolute top-20 left-10 text-[200px] opacity-20">🎾</div>
          <div className="absolute bottom-10 right-10 text-[150px] opacity-20">🏆</div>
        </div>

        {/* Navigation */}
        <nav className="relative z-10 max-w-7xl mx-auto px-4 py-6 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-4xl">🎾</span>
            <span className="text-2xl font-bold text-white">Tennis Fantasy</span>
          </div>
          <div className="flex items-center gap-4">
            <Link
              href="/players"
              className="text-green-100 hover:text-white transition-colors"
            >
              Players
            </Link>
            <Link
              href="/leaderboard"
              className="text-green-100 hover:text-white transition-colors"
            >
              Leaderboard
            </Link>
            <Link
              href="/login"
              className="px-6 py-2 bg-white/10 hover:bg-white/20 text-white rounded-full transition-colors"
            >
              Sign In
            </Link>
          </div>
        </nav>

        {/* Hero Content */}
        <div className="relative z-10 max-w-7xl mx-auto px-4 py-24 text-center">
          <h1 className="text-5xl md:text-7xl font-bold text-white mb-6 leading-tight">
            Build Your Dream<br />
            <span className="bg-gradient-to-r from-yellow-400 to-yellow-200 bg-clip-text text-transparent">
              Tennis Team
            </span>
          </h1>
          <p className="text-xl md:text-2xl text-green-100 mb-8 max-w-3xl mx-auto">
            Draft real ATP & WTA players, compete in fantasy leagues, and earn points
            based on their real-world tournament performances.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              href="/login"
              className="px-8 py-4 bg-gradient-to-r from-yellow-400 to-yellow-500 text-green-900 font-bold text-lg rounded-full hover:from-yellow-300 hover:to-yellow-400 transition-all transform hover:scale-105 shadow-xl"
            >
              Start Playing Free
            </Link>
            <Link
              href="/players"
              className="px-8 py-4 bg-white/10 backdrop-blur-lg text-white font-semibold text-lg rounded-full hover:bg-white/20 transition-all border border-white/20"
            >
              Browse Players
            </Link>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="bg-black/20 py-24">
        <div className="max-w-7xl mx-auto px-4">
          <h2 className="text-3xl md:text-4xl font-bold text-white text-center mb-16">
            How It Works
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Feature 1 */}
            <div className="bg-white/5 backdrop-blur-lg rounded-2xl p-8 border border-white/10 text-center hover:border-green-400/50 transition-colors">
              <div className="text-5xl mb-4">🎯</div>
              <h3 className="text-xl font-bold text-white mb-3">Draft Players</h3>
              <p className="text-green-100">
                Join a league and draft your favorite ATP and WTA players in a snake-style draft.
              </p>
            </div>

            {/* Feature 2 */}
            <div className="bg-white/5 backdrop-blur-lg rounded-2xl p-8 border border-white/10 text-center hover:border-green-400/50 transition-colors">
              <div className="text-5xl mb-4">📊</div>
              <h3 className="text-xl font-bold text-white mb-3">Earn Points</h3>
              <p className="text-green-100">
                Score points when your players win matches, tournaments, and achieve upsets.
              </p>
            </div>

            {/* Feature 3 */}
            <div className="bg-white/5 backdrop-blur-lg rounded-2xl p-8 border border-white/10 text-center hover:border-green-400/50 transition-colors">
              <div className="text-5xl mb-4">🏆</div>
              <h3 className="text-xl font-bold text-white mb-3">Win Leagues</h3>
              <p className="text-green-100">
                Compete against friends in head-to-head matchups and climb the leaderboard.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Scoring Section */}
      <div className="py-24">
        <div className="max-w-7xl mx-auto px-4">
          <h2 className="text-3xl md:text-4xl font-bold text-white text-center mb-6">
            Scoring System
          </h2>
          <p className="text-green-100 text-center mb-12 max-w-2xl mx-auto">
            Earn points based on your players&apos; real tournament performances
          </p>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 max-w-4xl mx-auto">
            <div className="bg-white/5 rounded-xl p-6 text-center">
              <p className="text-3xl font-bold text-green-400 mb-2">+10</p>
              <p className="text-green-100 text-sm">Match Win</p>
            </div>
            <div className="bg-white/5 rounded-xl p-6 text-center">
              <p className="text-3xl font-bold text-green-400 mb-2">+15</p>
              <p className="text-green-100 text-sm">Grand Slam Win</p>
            </div>
            <div className="bg-white/5 rounded-xl p-6 text-center">
              <p className="text-3xl font-bold text-green-400 mb-2">+5</p>
              <p className="text-green-100 text-sm">Upset Bonus</p>
            </div>
            <div className="bg-white/5 rounded-xl p-6 text-center">
              <p className="text-3xl font-bold text-yellow-400 mb-2">+50</p>
              <p className="text-green-100 text-sm">Grand Slam Title</p>
            </div>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="py-16 bg-gradient-to-r from-green-600 to-emerald-600">
        <div className="max-w-4xl mx-auto px-4 text-center">
          <h2 className="text-3xl md:text-4xl font-bold text-white mb-4">
            Ready to Play?
          </h2>
          <p className="text-green-100 mb-8 text-lg">
            Join thousands of tennis fans competing in fantasy leagues
          </p>
          <Link
            href="/login"
            className="inline-block px-10 py-4 bg-white text-green-700 font-bold text-lg rounded-full hover:bg-green-50 transition-colors shadow-xl"
          >
            Create Free Account
          </Link>
        </div>
      </div>

      {/* Footer */}
      <footer className="bg-black/30 py-8">
        <div className="max-w-7xl mx-auto px-4 text-center text-green-200 text-sm">
          <p>© 2024 Tennis Fantasy. Built for tennis fans, by tennis fans.</p>
          <p className="mt-2">Powered by SportsRadar Tennis API</p>
        </div>
      </footer>
    </div>
  );
}
