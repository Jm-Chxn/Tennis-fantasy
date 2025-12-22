'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { supabaseAuth, User } from '@/lib/supabase';
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
        const currentUser = supabaseAuth.getUser();
        setUser(currentUser);
        setLoading(false);
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
