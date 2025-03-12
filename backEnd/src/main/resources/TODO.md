### 기능

[//]: # (- 경매가 종료되면 경매 Table 의 상태를 Finished 로 변경)

[//]: # (  - `방법:` ttl이 만료되었을 때 처리하기.)

[//]: # (- 경매의 startTime 이 되면, 경매 Table 의 상태를 Ongoing 으로 변경)

[//]: # (  - FE 에서 경매 상태를 확인할 때, Ongoing 인 경우에만 입찰 가능하도록 설정)

[//]: # (    - `방법:` ???)

[//]: # (  - BE 에서 startTime, endTime 을 FE 로 전달하고, &#40;타이머?&#41; 기능으로 경매 상태를 변경할 수 있도록 설정)

[//]: # (    - FE 에서 경매 상태가 변경되면 BE 의 Auction Table 에서도 상태가 변경되어야 함)

[//]: # (      - `방법:` ???)

예외중에서 ongoing -> 문제가 있나여?
저기 프론트단에서 엔드포인트 받자.
ongoing -> upcomming

상태 필드를 더 이상 관리하지 않기로 결정
- status 필드, dto(request, response) 등등의 클래스에서 상태(status)필드를 담고 있는 경우 수정하기
- 이제 모든 사용자의 첫 입찰 시, Redis 에서 조회한 결과는 절대 NULL 이 될 수 없으므로 관련 로직 수정 : [BidService]
  - `방법:` service가서 null 조건문 제거
  - 입찰
  
### 예외처리
  - 경매 상태에 따른 예외처리문 수정 : [AuctionService - getAuctionWithValidation 메서드]
  - 관리자 - 경매 등록시 검증 조건문 수정 : [AuctionService - createAuction 메서드]

### 검증
- status 에 따라 변경되는 로직 Check 하기!





현재 1,000,000원
min bid - 5,000

A. 15,000원
B. 20,100원

1. 5,000(min_bid)

5,000 50,000 500,000
-------------------
입력(place-hold) 15,000



