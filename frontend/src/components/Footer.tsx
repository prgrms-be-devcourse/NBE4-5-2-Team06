import Link from "next/link";
import { Separator } from "@/components/ui/separator";

const footerLinks = {
  services: {
    title: "서비스",
    links: [
      { name: "경매 참여하기", href: "/auctions" },
      { name: "경매 등록하기", href: "/auctions/create" },
      { name: "실시간 경매", href: "/live-auctions" },
      { name: "인기 경매", href: "/popular" },
    ],
  },
  support: {
    title: "고객지원",
    links: [
      { name: "이용가이드", href: "/guide" },
      { name: "자주묻는질문", href: "/faq" },
      { name: "공지사항", href: "/notice" },
      { name: "문의하기", href: "/contact" },
    ],
  },
  company: {
    title: "회사소개",
    links: [
      { name: "About Us", href: "/about" },
      { name: "이용약관", href: "/terms" },
      { name: "개인정보처리방침", href: "/privacy" },
      { name: "제휴문의", href: "/partnership" },
    ],
  },
};

export function Footer() {
  return (
    <footer className="w-full border-t bg-background">
      <div className="container px-4 md:px-6 py-8 md:py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div className="space-y-4">
            <Link href="/" className="flex items-center space-x-2">
              <span className="font-bold text-2xl">NBE</span>
            </Link>
            <p className="text-sm text-muted-foreground">
              NBE는 신뢰할 수 있는 경매 플랫폼입니다. 
              투명하고 안전한 거래를 약속드립니다.
            </p>
          </div>
          {Object.values(footerLinks).map((section) => (
            <div key={section.title} className="space-y-4">
              <h4 className="font-semibold">{section.title}</h4>
              <ul className="space-y-2">
                {section.links.map((link) => (
                  <li key={link.name}>
                    <Link 
                      href={link.href}
                      className="text-sm text-muted-foreground hover:text-foreground transition-colors"
                    >
                      {link.name}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
        <Separator className="my-8" />
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <p className="text-sm text-muted-foreground">
            © 2024 NBE. All rights reserved.
          </p>
          <div className="flex items-center gap-4">
            <Link 
              href="https://github.com/your-repo" 
              target="_blank" 
              rel="noopener noreferrer"
              className="text-muted-foreground hover:text-foreground"
            >
              GitHub
            </Link>
            <Link 
              href="https://twitter.com/your-account" 
              target="_blank" 
              rel="noopener noreferrer"
              className="text-muted-foreground hover:text-foreground"
            >
              Twitter
            </Link>
          </div>
        </div>
      </div>
    </footer>
  );
} 