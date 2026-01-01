// Supabase Client Configuration
// 
// To use Supabase, you need to:
// 1. Create a Supabase project at https://supabase.com
// 2. Get your project URL and anon key from Settings -> API
// 3. Add these environment variables to your .env.local file:
//    NEXT_PUBLIC_SUPABASE_URL=your-project-url
//    NEXT_PUBLIC_SUPABASE_ANON_KEY=your-anon-key

import { createClient, User as SupabaseUser } from '@supabase/supabase-js';

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL || 'https://placeholder.supabase.co';
const supabaseKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY || 'placeholder-key';

// Initialize Supabase client
export const supabase = createClient(supabaseUrl, supabaseKey);

export interface User {
    id: string;
    email: string;
    displayName: string;
}

// Helper to transform Supabase user to our User type
const mapSupabaseUser = (u: SupabaseUser | null): User | null => {
    if (!u) return null;
    return {
        id: u.id,
        email: u.email!,
        displayName: u.user_metadata?.display_name || u.email?.split('@')[0] || 'User',
    };
};

export const supabaseAuth = {
    // Sign up new user
    signUp: async (email: string, password: string, displayName: string): Promise<User> => {
        const { data, error } = await supabase.auth.signUp({
            email,
            password,
            options: {
                data: {
                    display_name: displayName,
                },
            },
        });

        if (error) throw error;
        if (!data.user) throw new Error('Signup failed');

        return mapSupabaseUser(data.user)!;
    },

    // Sign in existing user
    signIn: async (email: string, password: string): Promise<User> => {
        const { data, error } = await supabase.auth.signInWithPassword({
            email,
            password,
        });

        if (error) throw error;
        if (!data.user) throw new Error('Login failed');

        return mapSupabaseUser(data.user)!;
    },

    // Sign out
    signOut: async (): Promise<void> => {
        const { error } = await supabase.auth.signOut();
        if (error) throw error;
    },

    // Get current user
    getUser: (): User | null => {
        // This is a synchronous check that might not be perfect for SSR
        // For accurate auth state, use onAuthStateChange listener
        // But for this simple implementation we'll try to get session
        // Note: getUser in supabase-js is async usually, but we can check session
        return null; // Will rely on AuthContext to manage state via async calls
    },

    // Get auth token (for API calls)
    getToken: async (): Promise<string | null> => {
        const { data: { session } } = await supabase.auth.getSession();
        return session?.access_token || null;
    },

    // Get current session user async
    getCurrentUser: async (): Promise<User | null> => {
        const { data: { user } } = await supabase.auth.getUser();
        return mapSupabaseUser(user);
    }
};

export type SupabaseAuth = typeof supabaseAuth;
