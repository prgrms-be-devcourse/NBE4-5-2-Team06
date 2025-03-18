const API_BASE_URL = "http://35.203.149.35:8080/api";

export const getAuctionDetail = async (auctionId: string) => {
  const res = await fetch(`${API_BASE_URL}/auctions/${auctionId}`);
  return res.json();
};

export const postBid = async (auctionId: string, bidRequest: any) => {
  const res = await fetch(`${API_BASE_URL}/auctions/${auctionId}/bids`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(bidRequest),
  });
  return res.json();
};
