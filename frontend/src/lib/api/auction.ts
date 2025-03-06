export const getAllAuctions = async () => {
  const res = await fetch(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/auctions`
  );
  if (!res.ok) throw new Error("경매 목록을 가져오는 데 실패했습니다.");
  return res.json();
};
