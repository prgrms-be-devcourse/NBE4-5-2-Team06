"use client";

import { useEffect, useRef, useState } from "react";
import { useParams } from "next/navigation";
import {
  connectStomp,
  subscribeToAuction,
  disconnectStomp,
  sendAuctionMessage,
} from "@/lib/socket";
import { getAuctionDetail } from "@/lib/api/auction";
import AuctionForm from "@/components/auction/AuctionForm";
import AuctionChat from "@/components/auction/AuctionChat";
import { Client } from "@stomp/stompjs";

interface Message {
  id: number;
  sender: string;
  text: string;
  isMe: boolean;
}

export default function AuctionPage() {
  const { auctionId } = useParams() as { auctionId: string };

  const [auction, setAuction] = useState<any>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [highestBid, setHighestBid] = useState<number>(0);
  const [timeLeft, setTimeLeft] = useState<string>("");

  const chatContainerRef = useRef<HTMLDivElement | null>(null);
  const [client, setClient] = useState<Client | null>(null);

  const token =
    typeof window !== "undefined"
      ? localStorage.getItem("accessToken") || ""
      : "";
  const myNickname =
    typeof window !== "undefined" ? localStorage.getItem("nickname") || "" : "";

  // ✅ STOMP 연결 및 구독 (토큰 포함)
  useEffect(() => {
    if (!token || !auctionId) return;

    const stompClient = connectStomp(token);
    setClient(stompClient); // 클라이언트 저장

    subscribeToAuction(stompClient, auctionId, (msg) => {
      console.log("[AuctionPage] 웹소켓 메시지 수신:", msg);

      setMessages((prev) => {
        if (
          prev.some(
            (m) => m.text === `${msg.currentBid.toLocaleString()}원 입찰!`
          )
        ) {
          return prev;
        }
        return [
          ...prev,
          {
            id: Date.now(),
            sender: msg.nickname || "익명",
            text: `${msg.currentBid.toLocaleString()}원 입찰!`,
            isMe: msg.nickname === myNickname,
          },
        ];
      });

      setHighestBid(msg.currentBid);
    });

    return () => disconnectStomp();
  }, [token, auctionId, myNickname]);

  // ✅ 경매 상세 조회
  useEffect(() => {
    (async () => {
      const data = await getAuctionDetail(auctionId);
      if (data?.data) {
        setAuction(data.data);
        setHighestBid(data.data.startPrice);
        calculateTimeLeft(data.data.endTime);
      }
    })();
  }, [auctionId]);

  // ✅ 남은 시간 실시간 반영
  useEffect(() => {
    if (!auction?.endTime) return;
    const interval = setInterval(
      () => calculateTimeLeft(auction.endTime),
      1000
    );
    return () => clearInterval(interval);
  }, [auction?.endTime]);

  // ✅ 남은 시간 계산 함수
  const calculateTimeLeft = (endTime: string) => {
    const end = new Date(endTime).getTime();
    const now = new Date().getTime();
    const diff = end - now;

    if (diff <= 0) return setTimeLeft("경매 종료");

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    setTimeLeft(
      days > 0
        ? `${days}일 ${hours}시 ${minutes}분 ${seconds}초 남음`
        : `${hours}시 ${minutes}분 ${seconds}초 남음`
    );
  };

  // ✅ 채팅 스크롤 자동 이동
  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop =
        chatContainerRef.current.scrollHeight;
    }
  }, [messages.length]);

  // ✅ 입찰 요청 (WebSocket 전송)
  const handleBid = async (amount: number) => {
    const userUuid = localStorage.getItem("userUuid") || "";
    if (!userUuid) return alert("로그인이 필요합니다.");

    // WebSocket 연결 상태 확인
    if (!client || !client.connected) {
      console.error("[AuctionPage] STOMP 연결되지 않음. 메시지 전송 실패.");
      alert("서버와 연결이 끊어졌습니다. 페이지를 새로고침 해주세요.");
      return;
    }

    console.log("[AuctionPage] 입찰 메시지 전송 시도:", {
      auctionId,
      userUuid,
      amount,
    });

    // WebSocket으로 입찰 메시지 전송
    sendAuctionMessage(
      "/app/auction/bid",
      { auctionId, amount }, // token 빼도 됨. 함수 안에서 합쳐주니까
      token // 여기에 토큰 명시
    );

    // ✅ 즉시 반영
    setMessages((prev) => [
      ...prev,
      {
        id: Date.now(),
        sender: myNickname,
        text: `${amount.toLocaleString()}원 입찰 완료!`,
        isMe: true,
      },
    ]);

    setHighestBid(amount);
  };

  if (!auction) return <p>Loading...</p>;

  // ✅ 남은 시간 색상
  const timeLeftColor =
    timeLeft !== "경매 종료" &&
    new Date(auction.endTime).getTime() - new Date().getTime() <= 5 * 60 * 1000
      ? "text-red-500"
      : "text-blue-600";

  return (
    <div className="flex flex-col md:flex-row max-w-7xl mx-auto border rounded-lg shadow-lg overflow-hidden my-8">
      {/* 좌측 - 상품 정보 */}
      <div className="md:w-2/3 w-full p-6 border-r overflow-y-auto max-h-[700px]">
        <h1 className="text-2xl font-bold mb-4">{auction.product?.name}</h1>
        <img
          src={auction.product?.imageUrl}
          alt="product"
          className="w-full h-80 object-cover rounded mb-4"
        />
        <p className="text-gray-700 mb-4">{auction.product?.description}</p>
        <p className="text-lg">
          시작가: {auction.startPrice?.toLocaleString()}원
        </p>
        <p className="text-xl font-bold mt-2">
          현재 최고 입찰가:{" "}
          <span className="text-3xl text-green-600">
            {highestBid.toLocaleString()}원
          </span>
        </p>
        <p className={`mt-4 font-semibold ${timeLeftColor}`}>{timeLeft}</p>
      </div>

      {/* 우측 - 채팅 + 입찰 */}
      <div className="md:w-1/3 w-full flex flex-col p-4 max-h-[700px]">
        <h2 className="text-lg font-semibold mb-2">실시간 입찰 로그</h2>
        <div
          ref={chatContainerRef}
          className="flex-1 border rounded p-2 overflow-y-auto space-y-2 bg-gray-50"
        >
          <AuctionChat messages={messages} />
        </div>
        <div className="mt-4">
          <AuctionForm
            highestBid={highestBid}
            minBid={auction.minBid || 1000}
            onBid={handleBid}
          />
        </div>
      </div>
    </div>
  );
}
