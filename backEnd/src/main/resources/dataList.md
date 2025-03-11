[//]: # (### 실시간 최고 입찰가)

[//]: # (- auctionId , Long,)

[//]: # (- currentPrice , Integer,)

[//]: # (- bidder, String,)

[//]: # (- bidTime , DataTime,)

[//]: # ()
[//]: # (### 실시간 입찰 내역 리스트)

[//]: # (- nickname, String,)

[//]: # (- amount , Integer,)

[//]: # (- bidTime , DataTime,)

[//]: # ()
[//]: # (### 경매 종료 및 낙찰 정보)

[//]: # (- Status, String,)

[//]: # (- nickname, String,)

[//]: # (- winningBid, Integer,)

[//]: # (- winTime, DataTime,)


### FE -> spring Server
- 로그인한 사용자 Id : user_uuid
- 사용자가 입력한 입찰금액 : amount
- 입찰 경매 Id : auctionId

### spring Server -> FE(백업)
- 경매Id : auctionId
- 로그인한 사용자 Id : user_uuid
- 입찰 금액 : amount
- 입찰 시간 : bidTime


### spring Server -> Redis Server
- (Redis Server -> Spring Server)에서 비교한 결과(최고가)
- userUUid
- auctionId
- keys:1
### spring Server -> DB
- (Redis Server -> Spring Server)에서 비교한 결과(최고가)
- consider: Master-Slave 구조로 구성하여 저장

### Redis Server -> Spring Server
- 사용자의 입찰가와 redis의 현재가(최고가) 중 더 높은 가격을 비교하기 위한 입찰가 조회

### Redis Server -> DB
  X

### DB -> spring Server
- 경매 시작 가격: START_PRICE