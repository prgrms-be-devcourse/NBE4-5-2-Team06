"use client";

import { useEffect, useState } from "react";
import dayjs from "dayjs";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

// ✅ 서버 응답 타입 (status 제거)
interface RawAuction {
  auctionId: number;
  productName: string;
  imageUrl: string;
  startPrice: number;
  currentPrice: number;
  startTime: string;
  endTime: string;
  nickname: string;
  winningBid: number;
  winTime: string | null;
}

// ✅ 프론트 사용 타입 (status 제거)
interface Auction {
  auctionId: number;
  productName: string;
  startTime: string;
  endTime: string;
  highestBid?: number;
  startPrice?: number;
  nickname?: string;
  winnerId?: number;
  imageUrl?: string;
}

export default function AdminAuctionListPage() {
  const [auctions, setAuctions] = useState<Auction[]>([]);
  const [filter, setFilter] = useState("all");

  const now = dayjs();

  // ✅ 데이터 가져오기
  const fetchAuctions = async () => {
    try {
      const token = localStorage.getItem("accessToken");
      const response = await fetch("http://35.203.149.35:8080/api/admin/auctions", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) throw new Error("Failed to fetch auctions");

      const result = await response.json();

      const normalizedAuctions: Auction[] = Array.isArray(result.data)
        ? result.data.map((a: RawAuction) => ({
          auctionId: a.auctionId,
          productName: a.productName,
          startTime: a.startTime,
          endTime: a.endTime,
          highestBid: a.currentPrice,
          startPrice: a.startPrice,
          nickname: a.nickname,
          winnerId: a.winningBid,
          imageUrl: a.imageUrl?.trim(),
        }))
        : [];

      setAuctions(normalizedAuctions);
    } catch (error) {
      console.error("❌ Error fetching auctions:", error);
    }
  };

  // ✅ Polling
  useEffect(() => {
    fetchAuctions();
    const interval = setInterval(fetchAuctions, 5000);
    return () => clearInterval(interval);
  }, []);

  // ✅ 상태 구분
  const ongoingAuctions = auctions.filter((a) =>
    now.isAfter(dayjs(a.startTime)) && now.isBefore(dayjs(a.endTime))
  );
  const upcomingAuctions = auctions.filter((a) => now.isBefore(dayjs(a.startTime)));
  const finishedAuctions = auctions.filter((a) => now.isAfter(dayjs(a.endTime)));

  // ✅ 필터 적용
  const applyFilter = (list: Auction[], type: "ONGOING" | "UPCOMING" | "FINISHED") => {
    if (filter === "all") return list;
    return filter === type ? list : [];
  };

  return (
    <div className="p-6 space-y-10"> {/* ✅ 리스트 간 여백 충분히 넓게 */}
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

      {/* ✅ 진행 중 */}
      {applyFilter(ongoingAuctions, "ONGOING").length > 0 && (
        <div className="mt-5">
          <h2 className="text-xl font-bold text-red-600 mb-6">진행 중인 경매</h2>
          <AuctionList auctions={applyFilter(ongoingAuctions, "ONGOING")} badge="LIVE" color="red" />
        </div>
      )}

      {/* ✅ 예정 */}
      {applyFilter(upcomingAuctions, "UPCOMING").length > 0 && (
        <div className="mt-16">
          <h2 className="text-xl font-bold text-yellow-500 mb-6">예정된 경매</h2>
          <AuctionList auctions={applyFilter(upcomingAuctions, "UPCOMING")} badge="예정" color="yellow" />
        </div>
      )}

      {/* ✅ 종료 */}
      {applyFilter(finishedAuctions, "FINISHED").length > 0 && (
        <div className="mt-16">
          <h2 className="text-xl font-bold text-gray-500 mb-6">종료된 경매</h2>
          <AuctionList auctions={applyFilter(finishedAuctions, "FINISHED")} badge="종료" color="gray" />
        </div>
      )}
    </div>
  );
}

// ✅ 리스트 컴포넌트 (상태에 따라 가격 표시)
const AuctionList = ({
  auctions,
  badge,
  color,
}: {
  auctions: Auction[];
  badge: string;
  color: "red" | "yellow" | "gray";
}) => (
  <div className="grid gap-6 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
    {auctions.map((auction) => (
      <Card key={auction.auctionId}>
        <CardHeader className="relative">
          <CardTitle>{auction.productName}</CardTitle>
          <span
            className={`absolute top-3 right-3 px-2 py-1 text-xs font-bold rounded-md ${color === "red"
                ? "bg-red-600 text-white"
                : color === "yellow"
                  ? "bg-yellow-400 text-black"
                  : "bg-gray-400 text-white"
              }`}
          >
            {badge}
          </span>
        </CardHeader>
        <CardContent>
          {auction.imageUrl ? (
            <img
              src={auction.imageUrl}
              alt={auction.productName}
              className="w-full h-48 object-cover rounded"
            />
          ) : (
            <p className="text-gray-500">이미지 없음</p>
          )}

          {/* ✅ 상태별 가격 표시 문구만 조정 */}
          <p className="mt-4 text-lg font-semibold text-red-600">
            {badge === "예정"
              ? `시작가: ${auction.startPrice?.toLocaleString()}원`
              : badge === "종료"
                ? `최종 낙찰가: ${auction.highestBid && auction.highestBid > 0
                  ? `${auction.highestBid.toLocaleString()}원`
                  : `${auction.startPrice?.toLocaleString()}원`
                }`
                : `현재 입찰가: ${auction.highestBid && auction.highestBid > 0
                  ? `${auction.highestBid.toLocaleString()}원`
                  : `${auction.startPrice?.toLocaleString()}원`
                }`}
          </p>

          <p className="text-sm mt-2">낙찰자: {auction.nickname ?? "없음"}</p>
          <p className="text-sm">낙찰자 ID: {auction.winnerId ?? "없음"}</p>
        </CardContent>

      </Card>
    ))}
  </div>
);
