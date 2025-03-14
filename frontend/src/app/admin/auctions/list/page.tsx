"use client";

import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

interface Auction {
  auctionId: number;
  productName: string;
  status: string;
  highestBid?: number;
  nickname?: string;
  winnerId?: number;
  imageUrl?: string;
}

export default function AdminAuctionListPage() {
  const [auctions, setAuctions] = useState<Auction[]>([]);
  const [filter, setFilter] = useState("all");

  useEffect(() => {
    async function fetchAuctions() {
      try {
        const token = localStorage.getItem("accessToken"); // 🔥 토큰 가져오기
        const response = await fetch("http://localhost:8080/api/admin/auctions", {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`, // 🔥 토큰 추가
          },
        });

        if (!response.ok) throw new Error("Failed to fetch auctions");
        const result = await response.json();
        console.log("📌 [경매 목록 데이터]:", result); // 📌 데이터 확인
        setAuctions(Array.isArray(result.data) ? result.data : []);
      } catch (error) {
        console.error("❌ Error fetching auctions:", error);
      }
    }
    fetchAuctions();
  }, []);

  const filteredAuctions = auctions.filter(auction => 
    auction && (filter === "all" || auction.status === filter)
  );

  return (
    <div className="p-6 space-y-4">
      <h1 className="text-2xl font-bold">경매 목록 (관리자)</h1>
      <Select onValueChange={setFilter} defaultValue="all">
        <SelectTrigger className="w-48">
          <SelectValue placeholder="전체" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="all">전체</SelectItem>
          <SelectItem value="UPCOMING">예정</SelectItem>
          <SelectItem value="ONGOING">진행 중</SelectItem>
          <SelectItem value="FINISHED">종료</SelectItem>
        </SelectContent>
      </Select>
      <Separator />
      <div className="grid gap-4 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
        {filteredAuctions.map(auction => (
          <Card key={auction.auctionId}>
            <CardHeader className="relative">
              <CardTitle>{auction.productName}</CardTitle>
              <span className={`absolute top-3 right-3 px-2 py-1 text-xs font-bold rounded-md ${
                auction.status === "ONGOING" ? "bg-red-600 text-white" : 
                auction.status === "UPCOMING" ? "bg-yellow-400 text-black" : 
                "bg-gray-400 text-white"
              }`}>
                {auction.status === "ONGOING" ? "LIVE" : 
                 auction.status === "UPCOMING" ? "예정" : "종료"}
              </span>
            </CardHeader>
            <CardContent>
              {auction.imageUrl ? (
                <img src={auction.imageUrl} alt={auction.productName} className="w-full h-48 object-cover rounded" />
              ) : (
                <p className="text-gray-500">이미지 없음</p>
              )}
              <p className="mt-2 text-lg font-semibold text-red-600">
                최고 입찰가: {auction.highestBid !== undefined ? `${auction.highestBid.toLocaleString()}원` : "없음"}
              </p>
              <p className="text-sm">낙찰자: {auction.nickname ?? "없음"}</p>
              <p className="text-sm">낙찰자 ID: {auction.winnerId ?? "없음"}</p>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
