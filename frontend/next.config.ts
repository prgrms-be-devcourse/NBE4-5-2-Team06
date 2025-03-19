/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: false,
  
  // 타입 검사 비활성화
  typescript: {
    ignoreBuildErrors: true,
  },
  
  // ESLint 검사 비활성화
  eslint: {
    ignoreDuringBuilds: true,
  },

  // 외부 이미지 도메인 등록
  images: {
    domains: [
      "store.storeimages.cdn-apple.com",
      "sitem.ssgcdn.com",
      "m.media-amazon.com",
      "image.idus.com",
      "www.sleepmed.or.kr",
      "www.biz-con.co.kr",
      "cdn.gpkorea.com", 
    ],
  },

  async rewrites() {
    return [
      {
        source: "/api/auctions",
        destination: "http://35.203.149.35:8080/api/auctions",
      },
    ];
  },
};

module.exports = nextConfig;
