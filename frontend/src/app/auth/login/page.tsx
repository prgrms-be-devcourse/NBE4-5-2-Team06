"use client";

import { useRouter } from "next/navigation";
import { useAuth } from "@/app/context/AuthContext";
import { loginUser } from "@/lib/api/auth";
import { LoginForm } from "@/components/auth/LoginForm";
import { useState } from "react";

export default function LoginPage() {
  const [error, setError] = useState("");
  const { setToken } = useAuth();
  const router = useRouter();

  const handleLogin = async (email: string, password: string) => {
    try {
      const { data, msg } = await loginUser(email, password);
      const { token, userUUID, nickname } = await loginUser(email, password);

      localStorage.setItem("accessToken", token);
      localStorage.setItem("userUUID", userUUID);
      localStorage.setItem("nickname", nickname);

      setToken(token);
      alert(msg); // 로그인 성공 메시지
      router.push("/"); // 로그인 성공 시 메인 페이지 이동
    } catch (err: any) {
      setError(err.message); // 백엔드에서 받은 에러 메시지를 UI에 표시
    }
  };

  return <LoginForm onSubmit={handleLogin} error={error} />;
}
