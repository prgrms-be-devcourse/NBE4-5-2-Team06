src/
├── app/
│   ├── (auth)/
│   │   ├── login/
│   │   ├── signup/
│   │   └── logout/
│   ├── (auction)/
│   │   ├── auctions/
│   │   │   ├── [auctionId]/
│   │   │   └── page.tsx
│   │   └── page.tsx
│   ├── admin/
│   │   └── auctions/
│   │       └── page.tsx
│   └── page.tsx
├── components/
│   ├── auth/
│   │   ├── LoginForm.tsx
│   │   └── SignupForm.tsx
│   ├── auction/
│   │   ├── AuctionCard.tsx
│   │   ├── AuctionDetail.tsx
│   │   ├── AuctionList.tsx
│   │   └── BidForm.tsx
│   └── common/
│       ├── Header.tsx
│       ├── Footer.tsx
│       └── Loading.tsx
├── lib/
│   ├── api/
│   │   ├── auth.ts
│   │   ├── auction.ts
│   │   └── bid.ts
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   └── useAuction.ts
│   └── utils/
│       ├── constants.ts
│       └── types.ts
└── styles/
    └── globals.css