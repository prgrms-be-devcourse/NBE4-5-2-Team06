"use client";

import { useEffect, useRef, useState } from "react";
import { useParams, useRouter } from "next/navigation";
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

interface Auction {
  product: {
    name: string;
    imageUrl: string;
    description: string;
  };
  startPrice: number;
  currentBid: number;
  minBid: number;
  endTime: string;
}

export default function AuctionPage() {
  const { auctionId } = useParams() as { auctionId: string };
  const router = useRouter();

  const [auction, setAuction] = useState<Auction | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [timeLeft, setTimeLeft] = useState<string>("");

  const chatContainerRef = useRef<HTMLDivElement | null>(null);
  const [client, setClient] = useState<Client | null>(null);

  const token = typeof window !== "undefined" ? localStorage.getItem("accessToken") || "" : "";
  const myNickname = typeof window !== "undefined" ? localStorage.getItem("nickname") || "" : "";

  useEffect(() => {
    if (!token) {
      alert("로그인이 필요합니다.");
      router.push("/");
    }
  }, [token, router]);

  useEffect(() => {
    if (!token || !auctionId) return;

    const stompClient = connectStomp(token);
    setClient(stompClient);

    subscribeToAuction(stompClient, auctionId, (msg) => {
      console.log("[AuctionPage] 웹소켓 메시지 수신:", msg);

      setMessages((prev) => {
        if (prev.some((m) => m.text === `${msg.currentBid.toLocaleString()}원 입찰!`)) return prev;
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

      setAuction((prev: Auction | null) => (prev ? { ...prev, currentBid: msg.currentBid } : prev));
    });

    return () => disconnectStomp();
  }, [token, auctionId, myNickname]);

  useEffect(() => {
    (async () => {
      const data = await getAuctionDetail(auctionId);
      if (data?.data) {
        setAuction(data.data);
        calculateTimeLeft(data.data.endTime);
      }
    })();
  }, [auctionId]);

  useEffect(() => {
    if (!auction?.endTime) return;
    const interval = setInterval(() => calculateTimeLeft(auction.endTime), 1000);
    return () => clearInterval(interval);
  }, [auction?.endTime]);

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

  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }
  }, [messages.length]);

  const handleBid = async (amount: number) => {
    const userUUID = localStorage.getItem("userUUID") || "";
    if (!userUUID) return alert("로그인이 필요합니다.");

    if (!client || !client.connected) {
      console.error("[AuctionPage] STOMP 연결되지 않음. 메시지 전송 실패.");
      alert("서버와 연결이 끊어졌습니다. 페이지를 새로고침 해주세요.");
      return;
    }

    console.log("[AuctionPage] 입찰 메시지 전송 시도:", { auctionId, userUUID, amount });

    sendAuctionMessage("/app/auction/bid", { auctionId, amount }, token);

    setAuction((prev: Auction | null) => (prev ? { ...prev, currentBid: amount } : prev));
  };

  if (!auction) return <p>Loading...</p>;

  const timeLeftColor = timeLeft !== "경매 종료" && new Date(auction.endTime).getTime() - new Date().getTime() <= 5 * 60 * 1000 ? "text-red-500" : "text-blue-600";

  return (
    <div className="flex flex-col md:flex-row max-w-7xl mx-auto border rounded-lg shadow-lg overflow-hidden my-8">
      <div className="md:w-2/3 w-full p-6 border-r overflow-y-auto max-h-[700px]">
        <h1 className="text-2xl font-bold mb-4">{auction.product?.name}</h1>
        <img src={auction.product?.imageUrl} alt="product" className="w-full h-80 object-cover rounded mb-4" />
        <p className="text-gray-700 mb-4">{auction.product?.description}</p>
        <p className="text-lg">시작가: {auction.startPrice?.toLocaleString()}원</p>
        <p className="text-xl font-bold mt-2">
          현재 최고 입찰가: <span className="text-3xl text-green-600">{auction.currentBid?.toLocaleString()}원</span>
        </p>
        <p className={`mt-4 font-semibold ${timeLeftColor}`}>{timeLeft}</p>
      </div>

      <div className="md:w-1/3 w-full flex flex-col p-4 max-h-[700px]">
        <h2 className="text-lg font-semibold mb-2">실시간 입찰 로그</h2>
        <div ref={chatContainerRef} className="border rounded p-2 overflow-y-auto space-y-2 bg-gray-50 h-[500px]">
          <AuctionChat messages={messages} />
        </div>
        <div className="mt-4">
          <AuctionForm highestBid={auction.currentBid} minBid={auction.minBid || 1000} onBid={handleBid} />
        </div>
      </div>
    </div>
  );
}