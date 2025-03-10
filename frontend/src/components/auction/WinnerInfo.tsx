interface WinnerInfoProps {
  winner: {
    user: {
      username: string;
    };
    winningBid: number;
    winTime: string;
  };
}

export default function WinnerInfo({ winner }: WinnerInfoProps) {
  return (
    <div className="p-4 bg-green-50 rounded-lg border border-green-200">
      <h3 className="text-lg font-semibold text-green-800 mb-2">낙찰 정보</h3>
      <div className="space-y-2">
        <p className="text-green-700">
          <span className="font-medium">낙찰자:</span> {winner.user.username}
        </p>
        <p className="text-green-700">
          <span className="font-medium">낙찰가:</span> {winner.winningBid.toLocaleString()}원
        </p>
        <p className="text-green-700">
          <span className="font-medium">낙찰 시간:</span>{' '}
          {new Date(winner.winTime).toLocaleString('ko-KR')}
        </p>
      </div>
    </div>
  );
} 