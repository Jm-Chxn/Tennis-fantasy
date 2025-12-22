// Supabase Client Configuration
// 
// To use Supabase, you need to:
// 1. Create a Supabase project at https://supabase.com
// 2. Get your project URL and anon key from Settings -> API
// 3. Add these environment variables to your .env.local file:
//    NEXT_PUBLIC_SUPABASE_URL=your-project-url
//    NEXT_PUBLIC_SUPABASE_ANON_KEY=your-anon-key

// For now, we'll use mock functions that you can replace with Supabase later

export interface User {
    id: string;
    email: string;
    displayName: string;
}

// Mock auth state (replace with Supabase when you have credentials)
let currentUser: User | null = null;

// Simulated Supabase Auth functions
export const supabaseAuth = {
    // Sign up new user
    signUp: async (email: string, password: string, displayName: string): Promise<User> => {
        // In production, this would call Supabase
        // const { data, error } = await supabase.auth.signUp({ email, password })

        const user: User = {
            id: 'mock-' + Date.now(),
            email,
            displayName,
        };
        currentUser = user;
        localStorage.setItem('tennis_fantasy_user', JSON.stringify(user));
        return user;
    },

    // Sign in existing user
    signIn: async (email: string, password: string): Promise<User> => {
        // In production, this would call Supabase
        // const { data, error } = await supabase.auth.signInWithPassword({ email, password })

        const user: User = {
            id: 'mock-' + Date.now(),
            email,
            displayName: email.split('@')[0],
        };
        currentUser = user;
        localStorage.setItem('tennis_fantasy_user', JSON.stringify(user));
        return user;
    },

    // Sign out
    signOut: async (): Promise<void> => {
        currentUser = null;
        localStorage.removeItem('tennis_fantasy_user');
    },

    // Get current user
    getUser: (): User | null => {
        if (currentUser) return currentUser;

        if (typeof window !== 'undefined') {
            const stored = localStorage.getItem('tennis_fantasy_user');
            if (stored) {
                currentUser = JSON.parse(stored);
                return currentUser;
            }
        }
        return null;
    },

    // Get auth token (for API calls)
    getToken: async (): Promise<string | null> => {
        // In production, this would get the Supabase JWT
        // const { data: { session } } = await supabase.auth.getSession()
        // return session?.access_token

        return currentUser ? 'mock-token-' + currentUser.id : null;
    },
};

// Export type for TypeScript
export type SupabaseAuth = typeof supabaseAuth;
