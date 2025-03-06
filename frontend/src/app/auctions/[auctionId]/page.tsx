"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogTitle,
} from "@/components/ui/dialog";

const API_BASE_URL = "http://localhost:8080/api";
const DUMMY_USER_UUID = "1741226850438-c16a946d-e423-4988-9803-da55a014fded";

export default function AuctionPage() {
  const params = useParams();
  const auctionId = params.auctionId as string;
  const router = useRouter();

  const [auction, setAuction] = useState<any>(null);
  const [highestBid, setHighestBid] = useState<number>(0);
  const [bidAmount, setBidAmount] = useState<number | "">("");
  const [isUserInput, setIsUserInput] = useState<boolean>(false);
  const [timeLeft, setTimeLeft] = useState<string>("");

  // ✅ 모달 열림/닫힘 제어용 state
  const [dialogOpen, setDialogOpen] = useState(false);

  // 채팅처럼 표시할 입찰 로그 예시 (실제로는 서버에서 가져오거나 WebSocket 등으로 수신)
  const [chatMessages, setChatMessages] = useState<
    { id: number; sender: string; text: string }[]
  >([
    { id: 1, sender: "system", text: "현재 최고 입찰가: 100000원" },
    { id: 2, sender: "user", text: "120000원 입찰합니다!" },
  ]);

  // 남은 시간 계산 함수
  const calculateTimeLeft = (endTime: string) => {
    const end = new Date(endTime).getTime();
    const now = new Date().getTime();
    const diff = end - now;

    if (diff <= 0) return "경매 종료";

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    return `${days}일 ${hours}시 ${minutes}분 ${seconds}초 남음`;
  };

  // auctionId가 바뀔 때만 초기 데이터 가져오기
  useEffect(() => {
    if (!auctionId) return;

    fetch(`${API_BASE_URL}/auctions/${auctionId}`)
      .then((res) => res.json())
      .then((data) => {
        if (data.code === "200") {
          setAuction(data.data);
          setHighestBid(data.data.startPrice);
          setBidAmount(data.data.startPrice);
          setTimeLeft(calculateTimeLeft(data.data.endTime));
        }
      });
  }, [auctionId]);

  // 남은 시간 주기적으로 갱신
  useEffect(() => {
    if (!auction?.endTime) return;

    const interval = setInterval(() => {
      setTimeLeft(calculateTimeLeft(auction.endTime));
    }, 1000);

    return () => clearInterval(interval);
  }, [auction?.endTime]);

  // 최고 입찰가가 바뀌었는데, 사용자가 직접 입력한 게 아니라면 bidAmount 동기화
  useEffect(() => {
    if (!isUserInput) {
      setBidAmount(highestBid);
    }
  }, [highestBid, isUserInput]);

  // 버튼으로 입찰 금액 증가
  const handleBidIncrease = (increaseAmount: number) => {
    setBidAmount((prevAmount) => {
      const currentAmount = prevAmount === "" ? highestBid : Number(prevAmount);
      return currentAmount + increaseAmount;
    });
    setIsUserInput(true);
  };

  // 입력으로 입찰 금액 변경
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value === "" ? "" : Number(e.target.value);
    if (value === "" || (!isNaN(value) && value > 0)) {
      setBidAmount(value);
      setIsUserInput(true);
    }
  };

  // 입찰 요청
  const placeBid = async () => {
    if (typeof bidAmount !== "number" || bidAmount <= highestBid) {
      alert("최고 입찰가보다 높은 금액을 입력해주세요.");
      return;
    }

    const bidRequest = {
      userUuid: DUMMY_USER_UUID,
      amount: bidAmount,
    };

    const response = await fetch(`${API_BASE_URL}/auctions/${auctionId}/bids`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(bidRequest),
    });

    const data = await response.json();
    if (data.code === "200") {
      setHighestBid(bidAmount);
      setIsUserInput(false);

      // 예시로, 채팅 로그에 추가
      setChatMessages((prev) => [
        ...prev,
        {
          id: prev.length + 1,
          sender: "user",
          text: `${bidAmount}원 입찰 완료!`,
        },
      ]);

      alert("입찰 성공!");
      // ✅ 입찰 성공 후 모달 닫고, 기존 페이지로 돌아가려면:
      setDialogOpen(false);
      // router.back(); // 혹은 router.push("/원하는경로");
    } else {
      alert("입찰 실패: " + data.msg);
      // ✅ 실패 시에도 모달 닫기
      setDialogOpen(false);
      // router.back(); // 혹은 router.push("/원하는경로");
    }
  };

  if (!auction) return <p>Loading...</p>;

  return (
    <div className="min-h-screen bg-white text-black flex flex-col">
      {/* 상단 헤더 - 필요 없으면 삭제 가능 */}
      <header className="bg-black text-white py-4 px-6 flex items-center justify-between">
        <h1 className="text-xl font-bold">My Auction</h1>
        <span className="text-sm text-gray-300">Auction ID: {auctionId}</span>
      </header>

      {/* 메인 영역: 좌/우 분할 */}
      <main className="flex flex-1 overflow-hidden">
        {/* 왼쪽: 상품 정보 섹션 */}
        <section className="w-full md:w-1/2 lg:w-2/5 border-r border-gray-200 p-6 overflow-auto">
          {/* 상품 이름 / 이미지 / 설명만 보여줌 */}
          <h2 className="text-2xl font-semibold mb-4">
            {auction.product.productName}
          </h2>
          <img
            src={auction.product.imageUrl}
            alt={auction.product.productName}
            className="w-full h-64 object-cover rounded mb-4"
          />
          <p className="text-gray-700 mb-2">{auction.product.description}</p>
        </section>

        {/* 오른쪽: 채팅/입찰 로그 섹션 */}
        <section className="flex-1 p-6 flex flex-col bg-gray-50">
          {/* 채팅 영역 헤더 */}
          <h2 className="text-xl font-bold mb-4">입찰 로그</h2>
          {/* 채팅 목록 */}
          <div className="flex-1 overflow-y-auto space-y-4 pr-2">
            {chatMessages.map((msg) => (
              <ChatMessage key={msg.id} sender={msg.sender} text={msg.text} />
            ))}
          </div>

          {/* 채팅 입력부(예시) */}
          <div className="mt-4 flex gap-2">
            <Input placeholder="채팅 메시지 입력..." className="flex-1" />
            <Button className="bg-blue-500 hover:bg-blue-600 text-white">
              전송
            </Button>
          </div>

          {/* 경매 시간/입찰하기: 채팅 아래 */}
          <div className="mt-4 border-t pt-4">
            <p className="text-blue-600 mb-2">{timeLeft}</p>
            <p className="mb-2">
              현재 최고 입찰가:{" "}
              <span className="font-semibold text-blue-500">
                {highestBid}원
              </span>
            </p>

            <div className="flex gap-2 mb-4">
              <Button
                className="bg-blue-500 hover:bg-blue-600 text-white"
                onClick={() => handleBidIncrease(auction.minBid)}
              >
                +{auction.minBid}원
              </Button>
              <Button
                className="bg-blue-500 hover:bg-blue-600 text-white"
                onClick={() => handleBidIncrease(auction.minBid * 10)}
              >
                +{auction.minBid * 10}원
              </Button>
              <Button
                className="bg-blue-500 hover:bg-blue-600 text-white"
                onClick={() => handleBidIncrease(auction.minBid * 100)}
              >
                +{auction.minBid * 100}원
              </Button>
            </div>

            <div className="flex items-center gap-2">
              <Input
                type="number"
                value={bidAmount}
                onChange={handleInputChange}
                className={`w-full ${
                  isUserInput ? "text-black" : "text-gray-500"
                }`}
              />
              {/* Dialog: 열림/닫힘을 dialogOpen으로 제어 */}
              <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
                <DialogTrigger asChild>
                  <Button
                    className="bg-blue-500 hover:bg-blue-600 text-white"
                    onClick={() => setDialogOpen(true)}
                  >
                    입찰하기
                  </Button>
                </DialogTrigger>
                <DialogContent>
                  <DialogTitle>입찰 확인</DialogTitle>
                  <Alert>
                    <AlertTitle>입찰 확인</AlertTitle>
                    <AlertDescription>
                      {bidAmount}원으로 입찰하시겠습니까?
                    </AlertDescription>
                  </Alert>
                  <div className="flex justify-end gap-2 mt-4">
                    {/* 모달 닫기 */}
                    <Button
                      variant="secondary"
                      onClick={() => setDialogOpen(false)}
                    >
                      취소
                    </Button>
                    <Button onClick={placeBid} variant="default">
                      입찰 진행
                    </Button>
                  </div>
                </DialogContent>
              </Dialog>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}

/**
 * ChatMessage: 채팅 버블 형태 컴포넌트
 * sender === "user" 일 때 오른쪽 파란색 버블,
 * sender === "system" (또는 기타)일 때 왼쪽 회색 버블로 예시 구현
 */
function ChatMessage({ sender, text }: { sender: string; text: string }) {
  const isUser = sender === "user";
  return (
    <div
      className={`flex ${isUser ? "justify-end" : "justify-start"} items-start`}
    >
      {/* 왼쪽 회색 or 오른쪽 파랑 버블 */}
      <div
        className={`${
          isUser
            ? "bg-blue-500 text-white"
            : "bg-gray-200 text-gray-700 border border-gray-300"
        } rounded-lg px-4 py-2 max-w-xs`}
      >
        <p className="text-sm">{text}</p>
      </div>
    </div>
  );
}
