export interface Auction {
  auctionId: number;
  productName: string;
  imageUrl: string;
  status: string;
  startTime: string;
  endTime: string;
  nickname?: string;
  winningBid?: number;
  winTime?: string;
}
