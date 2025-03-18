"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";

export function Header() {
  const router = useRouter();
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // 로그인 상태 확인
  useEffect(() => {
    // 초기 로그인 상태 확인
    checkLoginStatus();

    // 로컬 스토리지 변경 이벤트 리스너 추가
    window.addEventListener('storage', handleStorageChange);
    
    // 커스텀 이벤트 리스너 추가
    window.addEventListener('login-status-change', checkLoginStatus);

    return () => {
      // 컴포넌트 언마운트 시 이벤트 리스너 제거
      window.removeEventListener('storage', handleStorageChange);
      window.removeEventListener('login-status-change', checkLoginStatus);
    };
  }, []);

  // 로그인 상태 확인 함수
  const checkLoginStatus = () => {
    const token = localStorage.getItem("accessToken");
    setIsLoggedIn(!!token);
  };

  // 로컬 스토리지 변경 감지 함수
  const handleStorageChange = (event: StorageEvent) => {
    if (event.key === "accessToken") {
      checkLoginStatus();
    }
  };

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
      const res = await fetch("http://35.203.149.35:8080/api/auth/logout", {
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

      setIsLoggedIn(false);
      alert("로그아웃 되었습니다.");
      router.push("/"); // 메인 페이지로 이동
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
          {!isLoggedIn ? (
            <>
              <Link href="/auth/login">
                <Button variant="ghost">로그인</Button>
              </Link>
              <Link href="/auth/register">
                <Button>회원가입</Button>
              </Link>
            </>
          ) : (
            <>
              <Link href="/mypage">
                <Button variant="outline">마이페이지</Button>
              </Link>
              <Button variant="outline" onClick={handleLogout}>
                로그아웃
              </Button>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
