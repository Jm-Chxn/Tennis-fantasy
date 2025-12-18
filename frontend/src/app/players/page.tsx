export default function Players() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            Player Selection
          </h1>
          <p className="text-gray-600">
            Browse and select the best tennis players for your fantasy team
          </p>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Position
              </label>
              <select className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option>All Positions</option>
                <option>Singles</option>
                <option>Doubles</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Country
              </label>
              <select className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option>All Countries</option>
                <option>Serbia</option>
                <option>Spain</option>
                <option>Poland</option>
                <option>Belarus</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Price Range
              </label>
              <select className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option>All Prices</option>
                <option>$10 - $15</option>
                <option>$15 - $20</option>
                <option>$20+</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Search
              </label>
              <input
                type="text"
                placeholder="Player name..."
                className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
        </div>

        {/* Players Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {/* Player Card 1 */}
          <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <span className="bg-green-100 text-green-800 text-xs font-medium px-2.5 py-0.5 rounded">
                  Rank #1
                </span>
                <span className="text-2xl font-bold text-blue-600">$15.50</span>
              </div>
              <div className="text-center mb-4">
                <div className="w-20 h-20 bg-gray-200 rounded-full mx-auto mb-3 flex items-center justify-center">
                  <span className="text-2xl font-bold text-gray-600">ND</span>
                </div>
                <h3 className="text-lg font-semibold text-gray-900">Novak Djokovic</h3>
                <p className="text-sm text-gray-500">Serbia</p>
              </div>
              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Points:</span>
                  <span className="font-medium">12,000</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Position:</span>
                  <span className="font-medium">Singles</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Form:</span>
                  <span className="text-green-600 font-medium">↑ +5.2%</span>
                </div>
              </div>
              <button className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors">
                Add to Team
              </button>
            </div>
          </div>

          {/* Player Card 2 */}
          <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <span className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded">
                  Rank #2
                </span>
                <span className="text-2xl font-bold text-blue-600">$14.75</span>
              </div>
              <div className="text-center mb-4">
                <div className="w-20 h-20 bg-gray-200 rounded-full mx-auto mb-3 flex items-center justify-center">
                  <span className="text-2xl font-bold text-gray-600">CA</span>
                </div>
                <h3 className="text-lg font-semibold text-gray-900">Carlos Alcaraz</h3>
                <p className="text-sm text-gray-500">Spain</p>
              </div>
              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Points:</span>
                  <span className="font-medium">11,000</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Position:</span>
                  <span className="font-medium">Singles</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Form:</span>
                  <span className="text-green-600 font-medium">↑ +3.8%</span>
                </div>
              </div>
              <button className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors">
                Add to Team
              </button>
            </div>
          </div>

          {/* Player Card 3 */}
          <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <span className="bg-purple-100 text-purple-800 text-xs font-medium px-2.5 py-0.5 rounded">
                  Rank #1
                </span>
                <span className="text-2xl font-bold text-blue-600">$16.00</span>
              </div>
              <div className="text-center mb-4">
                <div className="w-20 h-20 bg-gray-200 rounded-full mx-auto mb-3 flex items-center justify-center">
                  <span className="text-2xl font-bold text-gray-600">IS</span>
                </div>
                <h3 className="text-lg font-semibold text-gray-900">Iga Swiatek</h3>
                <p className="text-sm text-gray-500">Poland</p>
              </div>
              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Points:</span>
                  <span className="font-medium">11,500</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Position:</span>
                  <span className="font-medium">Singles</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Form:</span>
                  <span className="text-green-600 font-medium">↑ +4.1%</span>
                </div>
              </div>
              <button className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors">
                Add to Team
              </button>
            </div>
          </div>

          {/* Player Card 4 */}
          <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <span className="bg-orange-100 text-orange-800 text-xs font-medium px-2.5 py-0.5 rounded">
                  Rank #3
                </span>
                <span className="text-2xl font-bold text-blue-600">$13.25</span>
              </div>
              <div className="text-center mb-4">
                <div className="w-20 h-20 bg-gray-200 rounded-full mx-auto mb-3 flex items-center justify-center">
                  <span className="text-2xl font-bold text-gray-600">DM</span>
                </div>
                <h3 className="text-lg font-semibold text-gray-900">Daniil Medvedev</h3>
                <p className="text-sm text-gray-500">Russia</p>
              </div>
              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Points:</span>
                  <span className="font-medium">10,000</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Position:</span>
                  <span className="font-medium">Singles</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Form:</span>
                  <span className="text-red-600 font-medium">↓ -1.2%</span>
                </div>
              </div>
              <button className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors">
                Add to Team
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
