// src/components/Header.tsx
"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";

export function Header() {
  const router = useRouter();

  // 로그아웃 처리 함수
  const handleLogout = async () => {
    try {
      // localStorage에서 토큰 가져오기
      const token = localStorage.getItem("accessToken");
      if (!token) {
        alert("로그인 정보가 없습니다.");
        return;
      }

      // 서버에 로그아웃 요청 (Authorization 헤더로 토큰 전송)
      const res = await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) {
        throw new Error("로그아웃 요청 실패");
      }

      // localStorage에 저장된 인증 정보 삭제
      localStorage.removeItem("accessToken");
      localStorage.removeItem("nickname");
      localStorage.removeItem("userUUID");
      // 필요하면 다른 키도 삭제

      alert("로그아웃 되었습니다.");
      router.push("/auth/login"); // 로그인 페이지 등으로 이동
    } catch (error) {
      console.error("로그아웃 실패:", error);
      alert("로그아웃 실패");
    }
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-14 items-center justify-between">
        <Link href="/" className="flex items-center space-x-2">
          <span className="font-bold text-xl">NBE</span>
        </Link>

        <nav className="flex items-center gap-4">
          <Link href="/auth/login">
            <Button variant="ghost">로그인</Button>
          </Link>
          <Link href="/auth/register">
            <Button>회원가입</Button>
          </Link>
          <Link href="/mypage">
            <Button variant="outline">마이페이지</Button>
          </Link>
          {/* 로그아웃 버튼 */}
          <Button variant="outline" onClick={handleLogout}>
            로그아웃
          </Button>
        </nav>
      </div>
    </header>
  );
}
