'use client';

import { useEffect, useState } from 'react';
import AuctionCloseButton from '@/components/auction/AuctionCloseButton';
import WinnerInfo from '@/components/auction/WinnerInfo';

interface AuctionDetail {
  auctionId: number;
  product: {
    productName: string;
    imageUrl: string;
    description: string;
  };
  startPrice: number;
  minBid: number;
  startTime: string;
  endTime: string;
  status: string;
  winner?: {
    user: {
      username: string;
    };
    winningBid: number;
    winTime: string;
  };
}

interface AuctionDetailProps {
  auctionId: string;
}

export default function AuctionDetail({ auctionId }: AuctionDetailProps) {
  const [auction, setAuction] = useState<AuctionDetail | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAuctionDetail = async () => {
      try {
        const response = await fetch(`/api/auctions/${auctionId}`);
        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.message || '경매 정보를 불러오는데 실패했습니다.');
        }

        setAuction(data.data);
      } catch (error) {
        setError(error instanceof Error ? error.message : '오류가 발생했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchAuctionDetail();
  }, [auctionId]);

  if (isLoading) {
    return <div className="p-4">로딩 중...</div>;
  }

  if (error || !auction) {
    return <div className="p-4 text-red-500">{error || '경매 정보를 찾을 수 없습니다.'}</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg shadow-lg overflow-hidden">
          {/* 상품 이미지 */}
          <div className="aspect-w-16 aspect-h-9 relative">
            <img
              src={auction.product.imageUrl}
              alt={auction.product.productName}
              className="object-cover w-full"
            />
          </div>

          {/* 경매 정보 */}
          <div className="p-6">
            <div className="flex justify-between items-start mb-6">
              <h1 className="text-2xl font-bold">{auction.product.productName}</h1>
              <div className="flex items-center space-x-2">
                <span className={`px-3 py-1 rounded-full text-sm ${
                  auction.status === 'ONGOING'
                    ? 'bg-green-100 text-green-800'
                    : auction.status === 'FINISHED'
                    ? 'bg-red-100 text-red-800'
                    : 'bg-gray-100 text-gray-800'
                }`}>
                  {auction.status === 'ONGOING' ? '진행 중' : 
                   auction.status === 'FINISHED' ? '종료됨' : '예정됨'}
                </span>
              </div>
            </div>

            <div className="space-y-4">
              <p className="text-gray-600">{auction.product.description}</p>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-500">시작가</p>
                  <p className="font-semibold">{auction.startPrice.toLocaleString()}원</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">최소 입찰 단위</p>
                  <p className="font-semibold">{auction.minBid.toLocaleString()}원</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">시작 시간</p>
                  <p className="font-semibold">
                    {new Date(auction.startTime).toLocaleString('ko-KR')}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">종료 시간</p>
                  <p className="font-semibold">
                    {new Date(auction.endTime).toLocaleString('ko-KR')}
                  </p>
                </div>
              </div>

              {/* 낙찰자 정보 */}
              {auction.status === 'FINISHED' && auction.winner && (
                <div className="mt-6">
                  <WinnerInfo winner={auction.winner} />
                </div>
              )}

              {/* 경매 종료 버튼 */}
              <div className="mt-6">
                <AuctionCloseButton
                  auctionId={auction.auctionId}
                  isOngoing={auction.status === 'ONGOING'}
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 