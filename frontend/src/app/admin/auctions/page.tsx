"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

// 경매 목록을 조회하는 컴포넌트
export function AuctionList() {
  const [auctions, setAuctions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchAuctions = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch("/api/admin/auctions");

      if (!response.ok) throw new Error("경매 목록 조회를 실패했습니다.");

      const data = await response.json();
      setAuctions(data.data);
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
                <p>낙찰자: {auction.nickname}</p>
                <p>낙찰가: {auction.winningBid}</p>
                <p>낙찰 시간: {auction.winTime}</p>
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

// 경매 상품 등록하는 페이지
export default function AdminAuctionCreatePage() {
  const [productName, setProductName] = useState("");
  const [startPrice, setStartPrice] = useState<number>(0);
  const [minBid, setMinBid] = useState<number>(0);
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [description, setDescription] = useState("");

  const handleSubmit = async () => {
    const response = await fetch("http://localhost:8080/api/admin/auctions", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        productName,
        startPrice,
        minBid,
        startTime,
        endTime,
        imageUrl,
        description,
      }),
    });

    const data = await response.json();

    if (response.ok) {
      alert("경매가 성공적으로 등록되었습니다!");
      setProductName("");
      setStartPrice(0);
      setMinBid(0);
      setStartTime("");
      setEndTime("");
      setImageUrl("");
      setDescription("");
    } else {
      alert(`경매 등록 실패: ${data.msg}`);
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-50 p-4">
      <Card className="w-full max-w-lg shadow-lg">
        <CardHeader>
          <CardTitle className="text-xl">경매 상품 등록하기</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          <div className="flex items-center gap-2">
            <label className="w-24">상품명:</label>
            <Input
              placeholder="상품명 입력"
              value={productName}
              onChange={(e) => setProductName(e.target.value)}
            />
          </div>
          <div className="flex items-center gap-2">
            <label className="w-24">시작 가격:</label>
            <Input
              type="number"
              placeholder="시작 가격 입력"
              value={startPrice}
              onChange={(e) => setStartPrice(Number(e.target.value))}
            />
          </div>
          <div className="flex items-center gap-2">
            <label className="w-24">최소 입찰가:</label>
            <Input
              type="number"
              placeholder="최소 입찰가 입력"
              value={minBid}
              onChange={(e) => setMinBid(Number(e.target.value))}
            />
          </div>
          <div className="flex items-center gap-2">
            <label className="w-24">시작 시간:</label>
            <Input
              type="datetime-local"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
            />
          </div>
          <div className="flex items-center gap-2">
            <label className="w-24">종료 시간:</label>
            <Input
              type="datetime-local"
              value={endTime}
              onChange={(e) => setEndTime(e.target.value)}
            />
          </div>
          <div className="flex items-center gap-2">
            <label className="w-24">이미지 URL:</label>
            <Input
              placeholder="이미지 URL 입력"
              value={imageUrl}
              onChange={(e) => setImageUrl(e.target.value)}
            />
          </div>
          <div className="flex items-center gap-2">
            <label className="w-24">상품 설명:</label>
            <Input
              placeholder="상품 설명 입력"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>
          <Button onClick={handleSubmit}>경매 등록하기</Button>
        </CardContent>
      </Card>
    </div>
  );
}
