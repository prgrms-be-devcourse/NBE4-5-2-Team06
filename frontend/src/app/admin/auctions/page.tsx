"use client";

import { useState } from "react";

export default function AuctionList() {
  const [auctions, setAuctions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchAuctions = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch("/api/admin/auctions"); // 백엔드 API 호출
      if (!response.ok) throw new Error("경매 목록 조회를 실패했습니다.");

      const data = await response.json();
      setAuctions(data.data); // API 응답의 'data' 배열 설정
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4">
      <button onClick={fetchAuctions} className="text-black">
        전체 상품 목록 조회
      </button>

      {loading && <p className="mt-4 text-gray-600">불러오는 중...</p>}
      {error && <p className="mt-4 text-red-500">{error}</p>}

      <ul className="mt-4 space-y-2">
        {auctions.map((auction) => (
          <li key={auction.auctionId} className="p-4 border rounded">
            <h2 className="font-semibold">{auction.productName}</h2>
            <p>상태: {auction.status}</p>
            <p>시작 시간: {auction.startTime}</p>
            <p>종료 시간: {auction.endTime}</p>

            {auction.status === "FINISHED" && (
              <>
                <p>낙찰자: {auction.nickname}</p> {/* 낙찰자 정보 */}
                <p>낙찰가: {auction.winningBid}</p> {/* 낙찰가 */}
                <p>낙찰 시간: {auction.winTime}</p> {/* 낙찰 시간 */}
              </>
            )}
            {auction.imageUrl && (
              <img
                src={auction.imageUrl}
                alt={auction.productName}
                className="w-32 mt-2"
              />
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
