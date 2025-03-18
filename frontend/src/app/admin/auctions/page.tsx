// src/app/admin/auctions/page.tsx
"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function AdminAuctionCreatePage() {
  const [productName, setProductName] = useState("");
  const [startPrice, setStartPrice] = useState<string>(""); // 문자열로 관리
  const [minBid, setMinBid] = useState<string>(""); // 문자열로 관리
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [description, setDescription] = useState("");

  const handleSubmit = async () => {
    // 숫자 변환
    const startPriceNumber = Number(startPrice);
    const minBidNumber = Number(minBid);
    const token = localStorage.getItem('accessToken');
  
    // 📌 [로그 1] 입력값 확인
    console.log("📌 [경매 등록 요청 데이터 확인]:", {
      productName,
      startPrice: startPriceNumber,
      minBid: minBidNumber,
      startTime,
      endTime,
      imageUrl,
      description,
    });
  
    // 📌 [로그 2] 토큰 확인
    console.log("📌 [전송할 토큰]:", token);
  
    if (!token) {
      alert("로그인이 필요합니다.");
      return;
    }
  
    try {
      const response = await fetch("http://35.203.149.35:8080/api/admin/auctions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify({
          productName,
          startPrice: startPriceNumber,
          minBid: minBidNumber,
          startTime,
          endTime,
          imageUrl,
          description,
        }),
      });
  
      // 📌 [로그 3] 응답 상태 코드 확인
      console.log("📌 [응답 상태]:", response.status);
  
      const data = await response.json();
  
      // 📌 [로그 4] 서버 응답 데이터 확인
      console.log("📌 [서버 응답 데이터]:", data);
  
      if (response.ok) {
        alert("경매가 성공적으로 등록되었습니다!");
        // 초기화
        setProductName("");
        setStartPrice("");
        setMinBid("");
        setStartTime("");
        setEndTime("");
        setImageUrl("");
        setDescription("");
      } else {
        alert(`경매 등록 실패: ${data.msg}`);
      }
    } catch (error) {
      // 📌 [로그 5] 에러 로그
      console.error("❌ [경매 등록 중 에러 발생]:", error);
      alert("경매 등록 중 에러가 발생했습니다.");
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
              onChange={(e) => setStartPrice(e.target.value)}
            />
          </div>
          <div className="flex items-center gap-2">
            <label className="w-24">최소 입찰가:</label>
            <Input
              type="number"
              placeholder="최소 입찰가 입력"
              value={minBid}
              onChange={(e) => setMinBid(e.target.value)}
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
