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
import { Button } from "@/components/ui/button";

export default function AuctionPage() {
  const [auctions, setAuctions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [timeLeft, setTimeLeft] = useState<{ [key: number]: string }>({});

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
      setAuctions(data.data);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const interval = setInterval(() => {
      const updatedTimes: { [key: number]: string } = {};
      const now = dayjs();

      auctions.forEach((auction) => {
        const start = dayjs(auction.startTime);
        const end = dayjs(auction.endTime);

        let targetTime;
        if (now.isBefore(start)) {
          targetTime = start;
        } else if (now.isBefore(end)) {
          targetTime = end;
        } else {
          return;
        }

        const diff = targetTime.diff(now);
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((diff % (1000 * 60)) / 1000);

        if (days > 0) {
          updatedTimes[auction.auctionId] = `${days}일 ${hours}시간 ${minutes}분 ${seconds}초`;
        } else {
          updatedTimes[auction.auctionId] = `${hours}시간 ${minutes}분 ${seconds}초`;
        }
      });

      setTimeLeft(updatedTimes);
    }, 1000);

    return () => clearInterval(interval);
  }, [auctions]);

  const now = dayjs();
  const ongoingAuctions = auctions.filter(
    (a) => now.isAfter(dayjs(a.startTime)) && now.isBefore(dayjs(a.endTime))
  );
  const upcomingAuctions = auctions.filter((a) => now.isBefore(dayjs(a.startTime)));

  return (
    <div className="p-8 space-y-8">
      {loading && <p className="text-gray-600">불러오는 중...</p>}
      {error && <p className="text-red-500">{error}</p>}

      <AuctionSection title="진행 중인 경매" auctions={ongoingAuctions} timeLeft={timeLeft} />
      <AuctionSection title="예정된 경매" auctions={upcomingAuctions} timeLeft={timeLeft} />
    </div>
  );
}

const AuctionSection = ({
  title,
  auctions,
  timeLeft,
}: {
  title: string;
  auctions: any[];
  timeLeft: { [key: number]: string };
}) => (
  <div>
    <h2 className="text-2xl font-bold mb-4">{title}</h2>
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      {auctions.length > 0 ? (
        auctions.map((auction) => (
          <AuctionCard
            key={auction.auctionId}
            auction={auction}
            timeLeft={timeLeft[auction.auctionId]}
            isOngoing={dayjs().isAfter(dayjs(auction.startTime))}
          />
        ))
      ) : (
        <p className="text-gray-500">표시할 경매가 없습니다.</p>
      )}
    </div>
  </div>
);

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
          <Image src={auction.imageUrl.trim()} alt={auction.productName} fill style={{ objectFit: "cover" }} />
        </div>
      )}

      <p className={`mt-2 ${isOngoing ? "text-red-600 font-bold" : "text-gray-600"}`}>
        현재가: {auction.currentBid?.toLocaleString()}원
      </p>

      <p className="text-gray-500 text-sm mt-2">
        {isOngoing ? "남은 시간" : "시작까지 남은 시간"}: {" "}
        <span className={`font-semibold ${checkDangerTime(timeLeft) ? "text-red-600" : "text-blue-600"}`}>{
          timeLeft ?? (isOngoing ? "종료됨" : "곧 시작")
        }</span>
      </p>

      <p className="text-sm text-gray-400 mt-2">접속자 수: {Math.floor(Math.random() * 20) + 1}명</p>
    </CardContent>

    <CardFooter>
      {isOngoing ? (
        <Link href={`/auctions/${auction.auctionId}`} className="w-full">
          <Button className="w-full bg-blue-600 hover:bg-blue-700 text-white">경매 참여하기</Button>
        </Link>
      ) : (
        <Button disabled className="w-full">경매 대기 중</Button>
      )}
    </CardFooter>
  </Card>
);

const checkDangerTime = (timeStr: string | undefined): boolean => {
  if (!timeStr) return false;
  if (timeStr.includes("일")) return false;
  const timeParts = timeStr.match(/(\d+)시간 (\d+)분 (\d+)초/);
  if (!timeParts) return false;
  const [_, hours, minutes, seconds] = timeParts.map(Number);
  return hours * 3600 + minutes * 60 + seconds <= 300;
};
