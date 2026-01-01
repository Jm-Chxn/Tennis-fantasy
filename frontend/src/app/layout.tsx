import type { Metadata } from "next";
import "./globals.css";
import { AuthProvider } from "@/contexts/AuthContext";

export const metadata: Metadata = {
  title: "Tennis Fantasy - Build Your Dream Tennis Team",
  description: "Draft real ATP/WTA players, build your roster, and earn points based on their tournament performances. Head-to-head matchups, playoffs, and bragging rights await!",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="antialiased">
        <AuthProvider>
          {children}
        </AuthProvider>
      </body>
    </html>
  );
}
