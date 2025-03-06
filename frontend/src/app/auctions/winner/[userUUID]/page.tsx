// src/app/auctions/winner/[userUUID]/page.tsx
'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/Button';

interface WinnerData {
  auctionId: number;
  productName: string;
  winningBid: number;
  winTime: string;
}

export default function AuctionWinnerPage() {
  const { userUUID } = useParams();
  const [winners, setWinners] = useState<WinnerData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchWinnerData = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/auctions/${userUUID}/winner`);
        if (!response.ok) throw new Error('데이터를 불러오는 데 실패했습니다.');
        
        const data = await response.json();
        console.log('API 응답 데이터:', data); // 데이터 구조 확인

        // API 응답이 배열인지 확인 후 설정
        if (Array.isArray(data)) {
          setWinners(data);
        } else if (data?.data && Array.isArray(data.data)) {
          setWinners(data.data); // 객체 내부의 data 필드에 배열이 있는 경우
        } else {
          setWinners([]); // 예외 상황 대비
        }
      } catch (err) {
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError('알 수 없는 오류 발생');
        }
      } finally {
        setLoading(false);
      }
    };

    if (userUUID) fetchWinnerData();
  }, [userUUID]);

  if (loading) return <p className="text-center">로딩 중...</p>;
  if (error) return <p className="text-center text-red-500">오류 발생: {error}</p>;
  if (winners.length === 0) return <p className="text-center">낙찰된 경매 내역이 없습니다.</p>;

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-50 p-4">
      <Card className="w-full max-w-lg shadow-lg">
        <CardHeader>
          <CardTitle className="text-xl">내 낙찰 내역</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          {winners.map((winner) => (
            <div key={winner.auctionId} className="border-b pb-2 mb-2">
              <p><strong>경매 ID:</strong> {winner.auctionId}</p>
              <p><strong>상품명:</strong> {winner.productName}</p>
              <p><strong>낙찰 금액:</strong> {winner.winningBid} 원</p>
              <p><strong>낙찰 시간:</strong> {new Date(winner.winTime).toLocaleString()}</p>
            </div>
          ))}
          <Button onClick={() => window.history.back()}>돌아가기</Button>
        </CardContent>
      </Card>
    </div>
  );
}
