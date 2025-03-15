"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";

interface User {
  nickname: string;
  email: string;
  profileImage?: string;
}

interface Auction {
  auctionId: number;
  productName: string;
  winningBid: number;
  winTime: string;
  imageUrl?: string; // imageUrl 속성 추가
}

export default function MyPage() {
  const { userUUID } = useParams();
  const [user, setUser] = useState<User | null>(null);
  const [auctions, setAuctions] = useState<Auction[]>([]);
  const [error, setError] = useState<string | null>(null);
  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

  useEffect(() => {
    if (!userUUID) {
      setError("사용자 정보를 찾을 수 없습니다.");
      return;
    }

    // 사용자 정보 가져오기
    fetch(`${API_BASE_URL}/auth/users/${userUUID}`)
      .then((res) => {
        if (!res.ok) {
          throw new Error(`사용자 API 오류: ${res.status}`);
        }
        return res.json();
      })
      .then((data) => setUser(data.data))
      .catch((error) => {
        console.error("사용자 정보 불러오기 오류:", error);
        setError("사용자 정보를 불러올 수 없습니다.");
      });

    // 낙찰 받은 경매 목록 가져오기
    fetch(`${API_BASE_URL}/auctions/${userUUID}/winner`)
      .then((res) => {
        if (!res.ok) {
          throw new Error(`낙찰 API 오류: ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log("받아온 낙찰 데이터:", data);
        if (!data || !Array.isArray(data.data)) {
          throw new Error("잘못된 응답 형식");
        }
        setAuctions(data.data);
      })
      .catch((error) => {
        console.error("경매 데이터 불러오기 오류:", error);
        setError("낙찰 정보를 불러올 수 없습니다.");
      });
  }, [userUUID]);

  return (
    <div className="max-w-2xl mx-auto p-6">
      {/* 오류 메시지 표시 */}
      {error && <p className="text-red-500 text-center">{error}</p>}

      {/* 프로필 정보 */}
      <div className="flex items-center gap-6 p-4 border rounded-lg shadow">
        <div className="w-20 h-20 bg-gray-300 rounded-full overflow-hidden">
          {user?.profileImage ? (
            <img src={user.profileImage} alt="Profile" className="w-full h-full object-cover" />
          ) : null}
        </div>
        <div>
          <p className="text-lg font-semibold">{user?.nickname || "닉네임"}</p>
          <p className="text-gray-600">{user?.email || "email@example.com"}</p>
        </div>
        <button className="ml-auto px-3 py-2 bg-blue-500 text-white rounded">
          수정
        </button>
      </div>

      {/* 낙찰 받은 경매 목록 */}
      <h2 className="text-xl font-bold mt-6">낙찰 받은 경매</h2>
      <div className="grid grid-cols-3 gap-4 mt-4">
        {auctions.length > 0 ? (
          auctions.map((auction) => (
            <div key={auction.auctionId} className="border rounded-lg p-2 shadow">
              <div className="w-full h-20 bg-gray-200 rounded overflow-hidden">
                {auction.imageUrl ? (
                  <img src={auction.imageUrl} alt={auction.productName} className="w-full h-full object-cover" />
                ) : null}
              </div>
              <p className="text-sm font-semibold mt-2">{auction.productName}</p>
              <p className="text-blue-500 font-bold">₩{auction.winningBid.toLocaleString()}</p>
              <p className="text-gray-500 text-xs">{new Date(auction.winTime).toLocaleString()}</p>
            </div>
          ))
        ) : (
          <p className="text-gray-500 mt-4">낙찰 받은 경매가 없습니다.</p>
        )}
      </div>
    </div>
  );
}
