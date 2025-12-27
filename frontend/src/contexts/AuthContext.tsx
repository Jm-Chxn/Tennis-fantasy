'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { supabaseAuth, User, supabase } from '@/lib/supabase';
import { authApi } from '@/lib/api';

interface AuthContextType {
    user: User | null;
    loading: boolean;
    signIn: (email: string, password: string) => Promise<void>;
    signUp: (email: string, password: string, displayName: string) => Promise<void>;
    signOut: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    // Check for existing session on mount
    useEffect(() => {
        // Check for current session
        const initSession = async () => {
            const user = await supabaseAuth.getCurrentUser();
            setUser(user);
            setLoading(false);
        };
        initSession();

        // Listen for auth changes
        const { data: { subscription } } = supabase.auth.onAuthStateChange(async (_event, session) => {
            if (session?.user) {
                const user = await supabaseAuth.getCurrentUser();
                if (user) {
                    // Automatically sync/register with backend on every session change
                    try {
                        await authApi.register({
                            supabaseId: user.id,
                            email: user.email,
                            displayName: user.displayName,
                        });
                    } catch (err) {
                        console.error('Failed to sync user with backend', err);
                    }
                }
                setUser(user);
            } else {
                setUser(null);
            }
            setLoading(false);
        });

        return () => {
            subscription.unsubscribe();
        };
    }, []);

    // Sign in
    const signIn = async (email: string, password: string) => {
        setLoading(true);
        try {
            const user = await supabaseAuth.signIn(email, password);

            // Sync with backend
            await authApi.register({
                supabaseId: user.id,
                email: user.email,
                displayName: user.displayName,
            });

            setUser(user);
        } finally {
            setLoading(false);
        }
    };

    // Sign up
    const signUp = async (email: string, password: string, displayName: string) => {
        setLoading(true);
        try {
            const user = await supabaseAuth.signUp(email, password, displayName);

            // Register with backend
            await authApi.register({
                supabaseId: user.id,
                email: user.email,
                displayName: user.displayName,
            });

            setUser(user);
        } finally {
            setLoading(false);
        }
    };

    // Sign out
    const signOut = async () => {
        await supabaseAuth.signOut();
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, loading, signIn, signUp, signOut }}>
            {children}
        </AuthContext.Provider>
    );
}

// Hook to use auth context
export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}
