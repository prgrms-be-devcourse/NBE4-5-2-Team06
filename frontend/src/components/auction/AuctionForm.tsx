// src/components/auction/AuctionForm.tsx
"use client";

import { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

interface AuctionFormProps {
  highestBid: number; // 현재 최고 입찰가
  minBid: number; // 최소 입찰 단위
  onBid: (amount: number) => void; // 입찰 함수
  canBid: boolean; // 🔑 버튼 활성화 여부
}

export default function AuctionForm({
  highestBid,
  minBid,
  onBid,
  canBid, // ✅ 비활성화 여부 prop 받기
}: AuctionFormProps) {
  const [amount, setAmount] = useState<number>(highestBid + minBid);
  const [isUserInput, setIsUserInput] = useState<boolean>(false); // 사용자가 직접 입력했는지 여부

  // 🟡 최고 입찰가가 바뀔 때 자동 반영 (사용자가 수동 입력 중이면 유지)
  useEffect(() => {
    if (!isUserInput) {
      setAmount(highestBid + minBid);
    }
  }, [highestBid, minBid, isUserInput]);

  // 💡 수동 입력 핸들러
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = Number(e.target.value);
    if (!isNaN(value) && value >= highestBid + minBid) {
      setAmount(value);
      setIsUserInput(true); // 사용자 직접 입력 감지
    }
  };

  // 💡 버튼 클릭으로 금액 증가
  const handleIncrease = (increase: number) => {
    setAmount((prev) => prev + increase);
    setIsUserInput(true); // 버튼도 사용자의 의도로 간주
  };

  // 입찰 버튼
  const handleBid = () => {
    if (amount >= highestBid + minBid) {
      onBid(amount);
      setIsUserInput(false); // 입찰 후에는 다시 자동 반영 모드로 전환
    } else {
      alert(`최소 ${highestBid + minBid}원 이상을 입력해주세요.`);
    }
  };

  return (
    <div className="space-y-4">
      {/* 가격 증가 버튼 */}
      <div className="flex gap-2">
        <Button
          className="bg-blue-500 hover:bg-blue-600 text-white flex-1"
          onClick={() => handleIncrease(minBid)}
          disabled={!canBid} // ✅ 비활성화 여부
          variant={!canBid ? "secondary" : "default"} // 비활성화 시 회색
        >
          +{minBid.toLocaleString()}원
        </Button>
        <Button
          className="bg-blue-500 hover:bg-blue-600 text-white flex-1"
          onClick={() => handleIncrease(minBid * 10)}
          disabled={!canBid}
          variant={!canBid ? "secondary" : "default"}
        >
          +{(minBid * 10).toLocaleString()}원
        </Button>
        <Button
          className="bg-blue-500 hover:bg-blue-600 text-white flex-1"
          onClick={() => handleIncrease(minBid * 100)}
          disabled={!canBid}
          variant={!canBid ? "secondary" : "default"}
        >
          +{(minBid * 100).toLocaleString()}원
        </Button>
      </div>

      {/* 직접 입력 */}
      <Input
        type="number"
        value={amount}
        onChange={handleInputChange}
        className="w-full text-center"
        placeholder={`${(highestBid + minBid).toLocaleString()}원 이상 입력`}
        disabled={!canBid} // ✅ 비활성화 여부
      />

      {/* 입찰 버튼 */}
      <Button
        onClick={handleBid}
        className="bg-green-600 hover:bg-green-700 text-white w-full"
        disabled={!canBid} // ✅ 비활성화 여부
        variant={!canBid ? "secondary" : "default"}
      >
        {amount.toLocaleString()}원 입찰하기
      </Button>
    </div>
  );
}
