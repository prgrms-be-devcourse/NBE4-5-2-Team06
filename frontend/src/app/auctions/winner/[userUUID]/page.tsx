// "use client";

// import { useEffect, useState } from "react";
// import { useParams } from "next/navigation";
// import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
// import { Button } from "@/components/ui/button";

// interface WinnerData {
//   auctionId: number;
//   productName: string;
//   winningBid: number;
//   winTime: string;
//   imageUrl?: string;
// }

// export default function AuctionWinnerPage() {
//   const { userUUID } = useParams();
//   const [winners, setWinners] = useState<WinnerData[]>([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState<string | null>(null);
//   const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

//   useEffect(() => {
//     const fetchWinnerData = async () => {
//       try {
//         const response = await fetch(`${API_BASE_URL}/auctions/${userUUID}/winner`);
//         if (!response.ok) throw new Error("데이터를 불러오는 데 실패했습니다.");
        
//         const data = await response.json();
//         console.log("API 응답 데이터:", data);

//         if (Array.isArray(data)) {
//           setWinners(data);
//         } else if (data?.data && Array.isArray(data.data)) {
//           setWinners(data.data);
//         } else {
//           setWinners([]);
//         }
//       } catch (err) {
//         if (err instanceof Error) {
//           setError(err.message);
//         } else {
//           setError("알 수 없는 오류 발생");
//         }
//       } finally {
//         setLoading(false);
//       }
//     };

//     if (userUUID) fetchWinnerData();
//   }, [userUUID]);

//   if (loading) return <p className="text-center">로딩 중...</p>;
//   if (error) return <p className="text-center text-red-500">오류 발생: {error}</p>;
//   if (winners.length === 0) return <p className="text-center">낙찰된 경매 내역이 없습니다.</p>;

//   return (
//     <div className="flex justify-center items-center min-h-screen bg-gray-50 p-4">
//       <Card className="w-full max-w-lg shadow-lg">
//         <CardHeader>
//           <CardTitle className="text-xl">내 낙찰 내역</CardTitle>
//         </CardHeader>
//         <CardContent className="flex flex-col gap-4">
//           {winners.map((winner) => (
//             <div key={winner.auctionId} className="border-b pb-2 mb-2">
//               <p><strong>경매 ID:</strong> {winner.auctionId}</p>
//               <p><strong>상품명:</strong> {winner.productName}</p>
//               <p><strong>낙찰 금액:</strong> ₩{winner.winningBid.toLocaleString()}</p>
//               <p><strong>낙찰 시간:</strong> {new Date(winner.winTime).toLocaleString()}</p>
//               <img
//                 src={winner.imageUrl || "/default-image.jpg"}
//                 alt={winner.productName}
//                 className="w-full h-40 object-cover mt-2"
//                 onError={(e) => (e.currentTarget.src = "/default-image.jpg")}
//               />
//             </div>
//           ))}
//           <Button onClick={() => window.history.back()}>돌아가기</Button>
//         </CardContent>
//       </Card>
//     </div>
//   );
// }
