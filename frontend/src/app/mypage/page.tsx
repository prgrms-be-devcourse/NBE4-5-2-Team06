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
  description?: string;
  winningBid: number;
  winTime: string;
  imageUrl?: string;
}

export default function MyPage() {
  const { userUUID } = useParams();
  const [user, setUser] = useState<User | null>(null);
  const [auctions, setAuctions] = useState<Auction[]>([]);
  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

  useEffect(() => {
    let uuid = userUUID || localStorage.getItem("userUUID");
    if (!uuid) {
      return;
    }

    console.log("현재 userUUID 값:", uuid);

    // 사용자 정보 가져오기
    fetch(`${API_BASE_URL}/auth/users/${uuid}`)
      .then((res) => res.ok ? res.json() : null)
      .then((data) => data?.data && setUser(data.data))
      .catch(console.error);

    // 낙찰 받은 경매 목록 가져오기
    fetch(`${API_BASE_URL}/auctions/${uuid}/winner`)
      .then((res) => res.ok ? res.json() : null)
      .then((data) => data?.data && Array.isArray(data.data) ? setAuctions(data.data) : [])
      .catch(console.error);
  }, [userUUID]);

  return (
    <div className="max-w-2xl mx-auto p-6">
      {/* 프로필 정보 */}
      <div className="flex items-center gap-6 p-4 border rounded-lg shadow">
        <div className="w-20 h-20 bg-gray-300 rounded-full overflow-hidden">
          <img src={user?.profileImage || "/default-profile.png"} alt="Profile" className="w-full h-full object-cover" />
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
                <img
                  src={auction.imageUrl || "/default-image.jpg"}
                  alt={auction.productName}
                  className="w-full h-full object-cover"
                  onError={(e) => (e.currentTarget.src = "/default-image.jpg")}
                />
              </div>
              <p className="text-sm font-semibold mt-2">{auction.productName}</p>
              <p className="text-xs text-gray-600">{auction.description || "설명 없음"}</p>
              <p className="text-blue-500 font-bold">낙찰가: {auction.winningBid.toLocaleString()}원</p>
              <p className="text-gray-500 text-xs">{new Date(auction.winTime).toLocaleString()}</p>
              <p className="text-red-500 text-sm font-semibold">결제 대기중</p>
            </div>
          ))
        ) : (
          <p className="text-gray-500 mt-4">낙찰 받은 경매가 없습니다.</p>
        )}
      </div>
    </div>
  );
}
