export default function Dashboard() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            Tennis Fantasy Dashboard
          </h1>
          <p className="text-gray-600">
            Manage your team, track performance, and dominate the leaderboard
          </p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow-md p-6 border-l-4 border-green-500">
            <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wide">
              Team Value
            </h3>
            <p className="text-3xl font-bold text-gray-900">$127.50</p>
            <p className="text-sm text-green-600">+$12.30 this week</p>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6 border-l-4 border-blue-500">
            <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wide">
              Total Points
            </h3>
            <p className="text-3xl font-bold text-gray-900">2,847</p>
            <p className="text-sm text-blue-600">+156 this week</p>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6 border-l-4 border-purple-500">
            <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wide">
              Ranking
            </h3>
            <p className="text-3xl font-bold text-gray-900">#12</p>
            <p className="text-sm text-purple-600">Top 15%</p>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6 border-l-4 border-orange-500">
            <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wide">
              Active Players
            </h3>
            <p className="text-3xl font-bold text-gray-900">8</p>
            <p className="text-sm text-orange-600">2 on bench</p>
          </div>
        </div>

        {/* Main Content Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Recent Matches */}
          <div className="lg:col-span-2 bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Recent Matches
            </h2>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center space-x-3">
                  <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                    <span className="text-green-600 font-semibold">W</span>
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">Djokovic vs Alcaraz</p>
                    <p className="text-sm text-gray-500">Australian Open Final</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-gray-900">+45 pts</p>
                  <p className="text-sm text-gray-500">2 days ago</p>
                </div>
              </div>

              <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center space-x-3">
                  <div className="w-8 h-8 bg-red-100 rounded-full flex items-center justify-center">
                    <span className="text-red-600 font-semibold">L</span>
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">Swiatek vs Sabalenka</p>
                    <p className="text-sm text-gray-500">Wimbledon Semifinal</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-gray-900">-12 pts</p>
                  <p className="text-sm text-gray-500">5 days ago</p>
                </div>
              </div>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Quick Actions
            </h2>
            <div className="space-y-3">
              <button className="w-full bg-green-600 text-white py-3 px-4 rounded-lg hover:bg-green-700 transition-colors">
                Make Transfers
              </button>
              <button className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 transition-colors">
                View Leaderboard
              </button>
              <button className="w-full bg-purple-600 text-white py-3 px-4 rounded-lg hover:bg-purple-700 transition-colors">
                Team Analysis
              </button>
              <button className="w-full bg-orange-600 text-white py-3 px-4 rounded-lg hover:bg-orange-700 transition-colors">
                Settings
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
