"use client";

import Link from "next/link";
import { useState } from "react";

export default function MyPage() {
  //경매 목록, 로딩 상태, 에러 메시지를 관리하는 상태 변수들
  const [auctions, setAuctions] = useState<any[]>([]); // 경매 목록을 저장할 상태
  const [loading, setLoading] = useState(false); // 데이터 로딩 중인지 여부를 관리하는 상태
  const [error, setError] = useState(""); // 오류 메시지를 저장할 상태태

  //경매 목록을 서버에서 가져오는 함수
  const fetchAuctions = async () => {
    setLoading(true);
    setError("");

    try {
      //API 호출
      const response = await fetch("/api/admin/auctions");
      //응답이 정상적이지 않다면 에러가 발생
      if (!response.ok) throw new Error("경매 목록 조회를 실패했습니다.");

      //응답 데이터
      const data = await response.json();
      setAuctions(data.data); //경매 목록을 상태에 저장
    } catch (err: any) {
      setError(err.message); //오류 발생 시 오류 메시지 저장
    } finally {
      setLoading(false); //로딩 종료료
    }
  };

  return (
    <div className="p-6">
      {/* 경매 목록을 불러오는 버튼 */}
      <button onClick={fetchAuctions} className="text-black">
        전체 상품 목록 조회
      </button>

      {/* 로딩 중일 때 불러오는 중 표시 */}
      {loading && <p className="mt-4 text-gray-600">불러오는 중...</p>}
      {/* 에러 발생시 오류 메시지 표시 */}
      {error && <p className="mt-4 text-red-500">{error}</p>}

      {/* 경매 목록을 보여주는 리스트 */}
      <ul className="mt-4 space-y-4">
        {auctions.map((auction) => (
          <li key={auction.auctionId} className="p-4 border rounded">
            <h2 className="font-semibold">{auction.productName}</h2>
            {/* 상품명*/}
            <p>상태: {auction.status}</p> {/* 경매 상태*/}
            <p>시작 시간: {auction.startTime}</p> {/* 경매 시작 시간*/}
            <p>종료 시간: {auction.endTime}</p> {/* 경매 종료 시간*/}
            {/* 경매 이미지 있다면 이미지 표시*/}
            {auction.imageUrl && (
              <img
                src={auction.imageUrl}
                alt={auction.productName} //이미지 대체 텍스트
                className="w-32 mt-2" //이미 스타일일
              />
            )}
          </li>
        ))}
      </ul>
      
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <main className="flex flex-col gap-8 row-start-2 items-center">
        <div className="relative w-[300px] h-[300px] md:w-[400px] md:h-[400px]">
          <Image
            src="/theMostHandsomePerson.png"
            alt="The Most Handsome Person"
            fill
            className="object-cover rounded-lg"
            priority
          />
        </div>
        <h1 className="text-4xl font-bold text-center">
          Welcome to NBE
        </h1>
        <p className="text-lg text-center text-muted-foreground">
          신뢰할 수 있는 경매 플랫폼
        </p>
      </main>
      <footer className="row-start-3 flex gap-6 flex-wrap items-center justify-center">
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4"
          href="https://nextjs.org/learn?utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app"
          target="_blank"
          rel="noopener noreferrer"
        >
          <Image
            aria-hidden
            src="/file.svg"
            alt="File icon"
            width={16}
            height={16}
          />
          Learn
        </a>
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4"
          href="https://vercel.com/templates?framework=next.js&utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app"
          target="_blank"
          rel="noopener noreferrer"
        >
          <Image
            aria-hidden
            src="/window.svg"
            alt="Window icon"
            width={16}
            height={16}
          />
          Examples
        </a>
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4"
          href="https://nextjs.org?utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app"
          target="_blank"
          rel="noopener noreferrer"
        >
          <Image
            aria-hidden
            src="/globe.svg"
            alt="Globe icon"
            width={16}
            height={16}
          />
          Go to nextjs.org →
        </a>
      </footer>
    </div>
  );
}
