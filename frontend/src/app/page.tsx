"use client";

import Link from "next/link";
import { useState, useEffect } from "react";
import Image from "next/image";
import dayjs from "dayjs";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/Button";

export default function AuctionPage() {
  const [auctions, setAuctions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [timeLeft, setTimeLeft] = useState<{ [key: number]: string }>({});

  // 1) 경매 목록 조회
  useEffect(() => {
    fetchAuctions();
  }, []);

  const fetchAuctions = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch("/api/auctions");
      if (!response.ok) throw new Error("경매 목록 조회 실패");
      const data = await response.json();

      // FINISHED 상태 제거
      let filtered = data.data.filter(
        (auction: any) => auction.status !== "FINISHED"
      );

      // 만약 데이터가 중복되어 들어온다면, auctionId 기준으로 중복 제거
      // filtered = filtered.filter(
      //   (auction: any, index: number, self: any[]) =>
      //     index === self.findIndex((a) => a.auctionId === auction.auctionId)
      // );

      setAuctions(filtered);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 2) 남은 시간 계산 (1초마다 업데이트)
  //    - ONGOING: endTime까지 남은 시간
  //    - UPCOMING: startTime까지 남은 시간
  useEffect(() => {
    const interval = setInterval(() => {
      const updatedTimes: { [key: number]: string } = {};
      const now = dayjs();

      auctions.forEach((auction) => {
        let targetTime;

        if (auction.status === "ONGOING") {
          targetTime = dayjs(auction.endTime);
        } else if (auction.status === "UPCOMING") {
          targetTime = dayjs(auction.startTime);
        } else {
          // FINISHED는 이미 목록에서 제거했으므로 처리 불필요
          return;
        }

        const diff = targetTime.diff(now);

        if (diff <= 0) {
          updatedTimes[auction.auctionId] =
            auction.status === "UPCOMING" ? "곧 시작" : "종료됨";
        } else {
          const hours = Math.floor(diff / (1000 * 60 * 60));
          const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
          const seconds = Math.floor((diff % (1000 * 60)) / 1000);
          updatedTimes[
            auction.auctionId
          ] = `${hours}시간 ${minutes}분 ${seconds}초`;
        }
      });

      setTimeLeft(updatedTimes);
    }, 1000);

    return () => clearInterval(interval);
  }, [auctions]);

  // 3) 진행 중 / 예정 경매 분리
  const ongoingAuctions = auctions.filter((a) => a.status === "ONGOING");
  const upcomingAuctions = auctions.filter((a) => a.status === "UPCOMING");

  return (
    <div className="p-8 space-y-8">
      {loading && <p className="text-gray-600">불러오는 중...</p>}
      {error && <p className="text-red-500">{error}</p>}

      {/* 진행 중인 경매 */}
      <div>
        <h2 className="text-2xl font-bold mb-4">진행 중인 경매</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {ongoingAuctions.map((auction) => (
            <AuctionCard
              key={auction.auctionId}
              auction={auction}
              timeLeft={timeLeft[auction.auctionId]}
              isOngoing={true}
            />
          ))}
        </div>
      </div>

      {/* 예정된 경매 */}
      <div>
        <h2 className="text-2xl font-bold mb-4">예정된 경매</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {upcomingAuctions.map((auction) => (
            <AuctionCard
              key={auction.auctionId}
              auction={auction}
              timeLeft={timeLeft[auction.auctionId]}
              isOngoing={false}
            />
          ))}
        </div>
      </div>
    </div>
  );
}

// 개별 경매 카드 컴포넌트
const AuctionCard = ({
  auction,
  timeLeft,
  isOngoing,
}: {
  auction: any;
  timeLeft: string;
  isOngoing: boolean;
}) => (
  <Card className="relative h-full flex flex-col justify-between">
    <CardHeader>
      <div className="flex justify-between items-center">
        <CardTitle>{auction.productName}</CardTitle>
        {isOngoing ? (
          <Badge variant="destructive">LIVE</Badge>
        ) : (
          <Badge className="bg-yellow-400 text-white">예정</Badge>
        )}
      </div>
    </CardHeader>

    <CardContent>
      {auction.imageUrl && (
        <div className="w-full h-48 relative rounded overflow-hidden mb-4">
          <Image
            src={auction.imageUrl.trim()}
            alt={auction.productName}
            fill // Next.js 13에서는 layout="fill" 대신 fill 사용
            style={{ objectFit: "cover" }}
          />
        </div>
      )}

      <p className="text-gray-600">
        현재가: {auction.currentPrice?.toLocaleString()}원
      </p>
      <p className="text-gray-500 text-sm mt-2">
        {isOngoing ? "남은 시간" : "시작까지 남은 시간"}:{" "}
        <span className="font-semibold text-blue-600">
          {timeLeft ?? (isOngoing ? "종료됨" : "곧 시작")}
        </span>
      </p>
      <p className="text-sm text-gray-400 mt-2">
        접속자 수: {Math.floor(Math.random() * 20) + 1}명
      </p>
    </CardContent>

    <CardFooter>
      {isOngoing ? (
        <Link href={`/auctions/${auction.auctionId}`} className="w-full">
          <Button className="w-full">경매 참여하기</Button>
        </Link>
      ) : (
        <Button disabled className="w-full">
          경매 대기 중
        </Button>
      )}
    </CardFooter>
  </Card>
);
