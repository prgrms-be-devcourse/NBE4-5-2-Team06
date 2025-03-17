"use client";

import {
  createContext,
  useContext,
  useState,
  ReactNode,
  useEffect,
} from "react";

// Context 안에 들어갈 데이터 타입 정의
interface AuthContextType {
  token: string | null;
  setToken: (token: string | null) => void;
}

// Context 기본값 (초기화)
const AuthContext = createContext<AuthContextType>({
  token: null,
  setToken: () => {},
});

// Provider 컴포넌트
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(null); // 처음에는 null로 시작

  // 새로고침 시 localstorage에서 토큰 읽어옴
  // useEffect(() => {
  //   const storedToken = localStorage.getItem("token");
  //   if (storedToken) {
  //     setToken(storedToken); // localStorage에서 토큰 읽기
  //   }
  // }, []); // 최고 1회 실행
  useEffect(() => {
    const storedToken = localStorage.getItem("accessToken"); // "token"에서 "accessToken"으로 변경
    if (storedToken) {
      setToken(storedToken);
    }
  }, []); // 최초 1회 실행

  return (
    <AuthContext.Provider value={{ token, setToken }}>
      {children}
    </AuthContext.Provider>
  );
};

// 쉽게 가져다 쓸 수 있는 훅
export const useAuth = () => useContext(AuthContext);