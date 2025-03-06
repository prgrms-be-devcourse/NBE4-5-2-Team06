// src/types/types.ts

// 경매 상품 타입 정의
export interface Auction {
  auctionId: number;
  productName: string;
  imageUrl: string;
  status: string;
  startTime: string;
  endTime: string;
  nickname?: string; // 낙찰자가 있을 경우
  winningBid?: number; // 낙찰가가 있을 경우
  winTime?: string; // 낙찰 시간
}
