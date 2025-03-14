"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";

interface LoginFormProps {
  onSubmit: (email: string, password: string) => void;
  error?: string;
}

export const LoginForm: React.FC<LoginFormProps> = ({ onSubmit, error }) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const router = useRouter();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(email, password);
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
      <div className="bg-white shadow-lg rounded-lg p-8 max-w-sm w-full text-center">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">로그인</h1>
        <form onSubmit={handleSubmit} className="flex flex-col space-y-4">
          <div className="text-left">
            <label
              htmlFor="email"
              className="block text-sm font-medium text-gray-700"
            >
              이메일
            </label>
            <input
              id="email"
              type="email"
              placeholder="이메일"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full p-3 border rounded-md focus:ring-2 focus:ring-blue-500 focus:outline-none"
              required
            />
          </div>
          <div className="text-left">
            <label
              htmlFor="password"
              className="block text-sm font-medium text-gray-700"
            >
              비밀번호
            </label>
            <input
              id="password"
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-3 border rounded-md focus:ring-2 focus:ring-blue-500 focus:outline-none"
              required
            />
          </div>
          {error && <p className="text-red-500 text-sm">{error}</p>}
          <button
            type="submit"
            className="w-full p-3 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition"
          >
            로그인
          </button>
          <p className="text-sm text-gray-500">
            회원이 아니신가요?{' '}
            <span
              onClick={() => router.push('/auth/register')}
              className="text-blue-500 hover:underline cursor-pointer"
            >
              회원가입
            </span>
          </p>
        </form>
      </div>
    </div>
  );
};