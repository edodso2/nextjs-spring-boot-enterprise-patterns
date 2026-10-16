import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Bundles a minimal Node server into .next/standalone for a small production image
  output: 'standalone',
};

export default nextConfig;
