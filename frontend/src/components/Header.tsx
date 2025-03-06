import Link from "next/link";
import { Button } from "@/components/ui/Button";

export function Header() {
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
        </nav>
      </div>
    </header>
  );
} 