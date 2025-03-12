package org.example.bidflow.domain.bid.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionBidRequest;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.service.AuctionService;
import org.example.bidflow.domain.bid.dto.model.response.BidCreateResponse;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.bid.repository.BidRepository;
import org.example.bidflow.domain.user.entity.User;
import org.example.bidflow.domain.user.service.UserService;
import org.example.bidflow.global.app.RedisCommon;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BidService {

    private final AuctionService auctionService;
    private final UserService userService;
    private final BidRepository bidRepository;
    private final RedisCommon redisCommon;

    /*@Transactional
    public BidCreateResponse createBid(Long auctionId, AuctionBidRequest request) {

        String key = "auction:" + auctionId;

        // 사용자 검증
        User user = userService.getUserByUuid(request.getUserUuid());

        // 경매 상태 검증 🚩
        Auction auction = auctionService.getAuctionWithValidation(auctionId);
        BidInfo currentBid = redisCommon.getHashAsObject(key, BidInfo.class);

        if (currentBid == null) { // 🚩

            BidInfo newBid =  BidInfo.builder()
                    .amount(request.getAmount())
                    .userUuid(request.getUserUuid())
                    .build();

            redisCommon.putObjectAsHash(key, newBid);
            currentBid = newBid;

        }

        if(request.getAmount() <= currentBid.getAmount() *//*amount*//*) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(), "입찰 금액이 현재 최고가보다 낮습니다.");
        }

        // 최소 입찰 단위 검증 🚩
        if (request.getAmount() < currentBid.getAmount() + auction.getMinBid()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(),
                    "입찰 금액이 최소 입찰 단위보다 작습니다. 최소 " + (currentBid.getAmount() + auction.getMinBid()) + "원 이상 입찰해야 합니다.");
        }
        redisCommon.putAllInHash(key, Map.of("amount", request.getAmount(), "userUuid", request.getUserUuid()));

        // 입찰 처리 (새로 생성하거나 금액 갱신)
        Bid bid = Bid.createBid(auction, user, request.getAmount(), LocalDateTime.now());

        // 입찰 저장
        bidRepository.save(bid);

        // BidDto 변환 후 반환
        return BidCreateResponse.from(bid);
    }*/
    @Transactional
    public BidCreateResponse createBid(Long auctionId, AuctionBidRequest request) {
        String hashKey = "auction:" + auctionId;
        LocalDateTime now = LocalDateTime.now();
        //- 입찰 과정에서 경매 진행중이 아닐 때(시간으로 비교) 예외 처리
        // 경매 시작 시간 <= 현재 시간 <= 경매 종료 시간

        // 사용자 및 경매 검증
        User user = userService.getUserByUuid(request.getUserUuid());
        Auction auction = auctionService.getAuctionWithValidation(auctionId);

        if(now.isBefore(auction.getStartTime())){
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(), "경매가 시작 전입니다.");
        }else if(now.isAfter(auction.getEndTime())){
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(), "경매가 종료 되었습니다.");
        }

        // Redis에서 현재 최고가 조회
        Integer amount = redisCommon.getFromHash(hashKey, "amount", Integer.class);

        // 최소 입찰 단위 검증
        if (request.getAmount() < amount + auction.getMinBid()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(),
                    "입찰 금액이 최소 입찰 단위보다 작습니다. 최소 " + (amount + auction.getMinBid()) + "원 이상 입찰해야 합니다.");
        }

        // 업커밍 -> 온고잉
        // 데이터 start -> redis
        // 스케줄링(upcoming -> ongoing) : TTL + amount(startPrice(DB) -> Redis in-memory) + 상태변화(upcoming -> ongoing)

        if (request.getAmount() <= amount) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(), "입찰 금액이 현재 최고가보다 낮습니다.");
        } else {
            // 최고가 갱신
            redisCommon.putInHash(hashKey, "amount", request.getAmount());
            redisCommon.putInHash(hashKey, "userUuid", request.getUserUuid());
        }

        /*//redisCommon.setExpire(hashKey, ttl.ofSeconds(secondsUntilExpire) );
        redisCommon.setExpireAt(hashKey, auction.getEndTime());

        // 최고가 갱신
        redisCommon.putInHash(hashKey, "amount", request.getAmount());
        redisCommon.putInHash(hashKey, "userUuid", request.getUserUuid());*/

        // DB 저장 (낙찰용 로그로 남김)
        Bid bid = Bid.createBid(auction, user, request.getAmount(), LocalDateTime.now());
        bidRepository.save(bid);

        return BidCreateResponse.from(bid);
    }

    // 0 -> o 105,0000
}

