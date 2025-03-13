/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: false,

  // 외부 이미지 도메인 등록
  images: {
    domains: [
      "store.storeimages.cdn-apple.com",
      "sitem.ssgcdn.com",
      "m.media-amazon.com",
    ],
  },

  async rewrites() {
    return [
      {
        source: "/api/auctions",
        destination: "http://localhost:8080/api/auctions",
      },
    ];
  },
};

module.exports = nextConfig;
