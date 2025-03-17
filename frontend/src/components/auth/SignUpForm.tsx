// src/components/auth/SignUpForm.tsx
"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import { signupUser } from "@/lib/api/auth";

export const SignUpForm = () => {
  const [email, setEmail] = useState("");
  const [nickname, setNickname] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [inputCode, setInputCode] = useState("");
  const [isVerified, setIsVerified] = useState(false);
  const [error, setError] = useState("");
  const [showVerificationInput, setShowVerificationInput] = useState(false);
  const [timer, setTimer] = useState(180);
  const [isBlocked, setIsBlocked] = useState(false);
  const router = useRouter();

  useEffect(() => {
    let countdown: ReturnType<typeof setInterval> | null = null;
    if (showVerificationInput && timer > 0 && !isVerified) {
      countdown = setInterval(() => setTimer((prev) => prev - 1), 1000);
    } else if (timer === 0 && !isVerified) {
      setIsBlocked(true);
      if (countdown) clearInterval(countdown);
    } else if (isVerified) {
      if (countdown) clearInterval(countdown);
    }
    return () => {
      if (countdown) clearInterval(countdown);
    };
  }, [showVerificationInput, timer, isVerified]);

  const handleEmailVerification = async () => {
    try {
      const response = await axios.post(
        "http://35.203.149.35:8080/api/auth/send-code",
        { email }
      );
      if (response.data.code === "200") {
        setShowVerificationInput(true);
        setTimer(180);
        setIsBlocked(false);
        alert(response.data.msg);
      }
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        setError(err.response?.data?.msg || "이메일 전송에 실패했습니다.");
      } else {
        setError("알 수 없는 오류가 발생했습니다.");
      }
    }
  };

  const handleCodeVerification = async () => {
    try {
      const response = await axios.post(
        "http://35.203.149.35:8080/api/auth/vertify",
        { email, code: inputCode }
      );
      if (response.data.code === "200") {
        setIsVerified(true);
        alert(response.data.msg);
      } else {
        setError(response.data.msg);
      }
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        setError(err.response?.data?.msg || "인증 확인에 실패했습니다.");
      } else {
        setError("알 수 없는 오류가 발생했습니다.");
      }
    }
  };
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!isVerified) {
      setError("이메일 인증을 완료해주세요.");
      return;
    }

    if (password !== confirmPassword) {
      setError("비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      const message = await signupUser(email, password, nickname);
      alert(`${message}`);
      router.push("/auth/login");
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 p-6">
      <div className="bg-white shadow-xl rounded-xl p-8 max-w-md w-full text-center">
        <h1 className="text-2xl font-semibold text-gray-800 mb-6">회원가입</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="relative">
            <input
              type="email"
              placeholder="이메일"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full p-3 pr-20 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-400 focus:outline-none"
              required
            />
            <button
              type="button"
              onClick={handleEmailVerification}
              className="absolute right-2 top-1/2 -translate-y-1/2 py-1 px-3 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition"
            >
              인증
            </button>
          </div>

          {showVerificationInput && (
            <>
              <div className="relative">
                <input
                  type="text"
                  placeholder="인증 코드 입력"
                  value={inputCode}
                  onChange={(e) => setInputCode(e.target.value)}
                  disabled={isBlocked || isVerified}
                  className={`w-full p-3 pr-20 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-400 focus:outline-none ${
                    isBlocked || isVerified ? "bg-gray-200" : ""
                  }`}
                />
                <button
                  type="button"
                  onClick={handleCodeVerification}
                  disabled={isBlocked || isVerified}
                  className="absolute right-2 top-1/2 -translate-y-1/2 py-1 px-3 bg-green-500 text-white rounded-md hover:bg-green-600 transition"
                >
                  {isVerified ? "완료" : "확인"}
                </button>
              </div>
              <p className="text-sm text-gray-500 mt-2">{`남은 시간: ${Math.floor(
                timer / 60
              )}:${(timer % 60).toString().padStart(2, "0")}`}</p>
            </>
          )}

          <input
            type="text"
            placeholder="닉네임"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-400 focus:outline-none"
            required
          />

          <input
            type="password"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-400 focus:outline-none"
            required
          />

          <input
            type="password"
            placeholder="비밀번호 확인"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-400 focus:outline-none"
            required
          />

          {error && <p className="text-red-500 text-sm text-left">{error}</p>}

          <button
            type="submit"
            className="w-full py-3 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition"
          >
            회원가입
          </button>

          <p className="text-sm text-gray-500">
            이미 회원이신가요?{" "}
            <span
              onClick={() => router.push("/auth/login")}
              className="text-blue-500 hover:underline cursor-pointer"
            >
              로그인
            </span>
          </p>
        </form>
      </div>
    </div>
  );
};
