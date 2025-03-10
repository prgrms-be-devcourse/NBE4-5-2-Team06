import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: "/api/auctions", // 프론트엔드에서 요청하는 경로
        destination: "http://localhost:8080/api/auctions", // 백엔드 실제 API 경로
      },
    ];
  },
};

export default nextConfig;
