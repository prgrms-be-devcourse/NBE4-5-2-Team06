"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { useToast } from "@/components/ui/Toast";

interface AuctionCloseButtonProps {
  auctionId: number;
  isOngoing: boolean;
}

export default function AuctionCloseButton({
  auctionId,
  isOngoing,
}: AuctionCloseButtonProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast();

  const handleClose = async () => {
    try {
      setIsLoading(true);
      const response = await fetch(`http://35.203.149.35:8080/api/auctions/${auctionId}/close`, {
        method: "POST",
      });

      const data = await response.json();

      if (response.ok) {
        toast({
          title: "경매가 성공적으로 종료되었습니다.",
          description: `낙찰자: ${data.data.user.username}`,
        });
        setIsOpen(false);
      } else {
        throw new Error(data.message || "경매 종료에 실패했습니다.");
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "오류",
        description:
          error instanceof Error
            ? error.message
            : "경매 종료 중 오류가 발생했습니다.",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Button
        variant="destructive"
        onClick={() => setIsOpen(true)}
        disabled={!isOngoing}
      >
        경매 종료하기
      </Button>

      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>경매 종료 확인</DialogTitle>
            <DialogDescription>
              정말로 이 경매를 종료하시겠습니까? 이 작업은 되돌릴 수 없습니다.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsOpen(false)}
              disabled={isLoading}
            >
              취소
            </Button>
            <Button
              variant="destructive"
              onClick={handleClose}
              disabled={isLoading}
            >
              {isLoading ? "처리 중..." : "종료하기"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
