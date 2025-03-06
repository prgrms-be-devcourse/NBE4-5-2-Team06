"use client";

import Link from "next/link";

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="p-6">
      <header className="flex justify-between items-center border-b pb-4 mb-4">
        <h1 className="text-2xl font-bold">경매 관리자 페이지</h1>
        <Link href="/admin/auction/new">
          <button className="bg-blue-500 text-white px-4 py-2 rounded">
            상품 등록
          </button>
        </Link>
      </header>
      <main>{children}</main>
    </div>
  );
}
