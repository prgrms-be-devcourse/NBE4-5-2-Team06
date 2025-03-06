"use client";

import { useState } from "react";

// AuctionList 컴포넌트 정의
export default function AuctionList() {
  // 상태 변수 설정
  const [auctions, setAuctions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  //경매 목록을 가져오는 비동기기 함수
  const fetchAuctions = async () => {
    setLoading(true);
    setError("");

    try {
      // 경매 목록을 요청청
      const response = await fetch("/api/admin/auctions");
      //응답이 정상적이지 않다면 에러 발생생
      if (!response.ok) throw new Error("경매 목록 조회를 실패했습니다.");

      //응답을 JSON 변환하고 auctions 상태에 저장
      const data = await response.json();
      setAuctions(data.data); // API 응답의 'data' 배열을 setAuctions를 통해 상태에 설정
    } catch (err: any) {
      setError(err.message); //에러 발생 시 해당 메시지를 상태에 저장
    } finally {
      setLoading(false); //비동기 작업이 끝나면 로딩 상태를 false로 설정
    }
  };

  return (
    <div className="p-4">
      {/* 경매 목록을 불러오는 버튼*/}
      <button onClick={fetchAuctions} className="text-black">
        전체 상품 목록 조회
      </button>
      {/*로딩 중일 때 불러오는 중 메시지 표시 */}
      {loading && <p className="mt-4 text-gray-600">불러오는 중...</p>}
      {/* 에러 발생시 오류 메시지 표시 */}
      {error && <p className="mt-4 text-red-500">{error}</p>}
      {/* 경매 목록을 표시*/}
      <ul className="mt-4 space-y-2">
        {/* auctions 배열을 순회하며 각 경매 항목을 리스트로 출력*/}
        {auctions.map((auction) => (
          <li key={auction.auctionId} className="p-4 border rounded">
            <h2 className="font-semibold">{auction.productName}</h2>{" "}
            {/* 경매 상품명*/}
            <p>상태: {auction.status}</p> {/* 경매 상태*/}
            <p>시작 시간: {auction.startTime}</p> {/*경매 시작 시간 */}
            <p>종료 시간: {auction.endTime}</p> {/* 경매 종료 시간*/}
            {/* 경매 상태가 'FINISHED*인 경우 낙찰자, 낙찰가, 낙찰 시간 표시*/}
            {auction.status === "FINISHED" && (
              <>
                <p>낙찰자: {auction.nickname}</p> {/* 낙찰자 정보 */}
                <p>낙찰가: {auction.winningBid}</p> {/* 낙찰가 */}
                <p>낙찰 시간: {auction.winTime}</p> {/* 낙찰 시간 */}
              </>
            )}
            {/* 경매 상품 이미지가 있을 경우 이미지를 표시*/}
            {auction.imageUrl && (
              <img
                src={auction.imageUrl} //경매 상품의 이미지 URL
                alt={auction.productName} // 이미지 대체 텍스트
                className="w-32 mt-2" // 이미지 스타일 (너비 32, 위에 마진을 추가가)
              />
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
