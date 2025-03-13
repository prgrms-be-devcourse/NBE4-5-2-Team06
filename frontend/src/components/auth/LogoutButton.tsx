"use client";

import { useRouter } from "next/navigation";
import { removeAuthData } from "@/lib/api/auth";

export const LogoutButton = () => {
  const router = useRouter();

  const handleLogout = async () => {
    try {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token: localStorage.getItem("accessToken") }),
      });

      removeAuthData(); // 로컬 스토리지에서 인증 정보 삭제
      alert("로그아웃 되었습니다.");
      router.push("/login");
    } catch (error) {
      console.error("로그아웃 실패", error);
    }
  };

  return (
    <button
      onClick={handleLogout}
      className="p-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition"
    >
      로그아웃
    </button>
  );
};
