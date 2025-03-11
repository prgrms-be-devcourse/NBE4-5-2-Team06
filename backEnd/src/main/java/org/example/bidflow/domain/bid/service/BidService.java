package org.example.bidflow.domain.bid.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionBidRequest;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.auction.service.AuctionService;
import org.example.bidflow.domain.bid.dto.model.response.BidCreateResponse;
import org.example.bidflow.domain.bid.dto.model.response.BidInfo;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.bid.repository.BidRepository;
import org.example.bidflow.domain.user.entity.User;
import org.example.bidflow.domain.user.service.UserService;
import org.example.bidflow.global.app.RedisCommon;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BidService {

    private final AuctionService auctionService;
    private final UserService userService;
    private final BidRepository bidRepository;
    private final RedisCommon redisCommon;
    private final RedisTemplate redisTemplate;
    private final AuctionRepository auctionRepository;

    @Transactional
    public BidCreateResponse createBid(Long auctionId, AuctionBidRequest request) {

        String key = "auction:" + auctionId;

        // 1. Redis에 데이터가 없으면 예외 발생
        // 수정전1: BidStringModel bidModel = redisCommon.getData(key, BidStringModel.class);

        // 사용자 검증
        User user = userService.getUserByUuid(request.getUserUuid());

        // 경매 상태 검증 🚩
        Auction auction = auctionService.getAuctionWithValidation(auctionId);

        // Integer currentHighestBid = redisCommon.getFromHash(key, "amount", Integer.class); // 수정2
        BidInfo currentBid = redisCommon.getHashAsObject(key, BidInfo.class);

        if (currentBid == null) { // 🚩
            /*redisCommon.putInHash(key, "amount", request.getAmount());
            redisCommon.putInHash(key,"userUuid",request.getUserUuid());*/
            /*redisCommon.putAllInHash(key, Map.of("amount", request.getAmount(), "userUuid", request.getUserUuid()));
            currentHighestBid = request.getAmount();*/

            BidInfo newBid =  BidInfo.builder()
                    .amount(request.getAmount())
                    .userUuid(request.getUserUuid())
                    .build();

            redisCommon.putObjectAsHash(key, newBid);
            currentBid = newBid;

            // 수정3
            /*// 2. Redis에 저장, UserUUid, Amount ,TTL
            redisCommon.putInHash(key,"amount",request.getAmount());
            redisCommon.putInHash(key,"userUuid",request.getUserUuid());
//            redisCommon.expire(key, 60); // 60초*/
        }

        // 최고가를 가져옴
//        수정:4 Integer amount = redisCommon.getFromHash(key, "amount", Integer.class);

        if(request.getAmount() <= currentBid.getAmount() /*amount*/) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(), "입찰 금액이 현재 최고가보다 낮습니다.");
        }

        // 최소 입찰 단위 검증 🚩
        if (request.getAmount() < currentBid.getAmount() + auction.getMinBid()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(),
                    "입찰 금액이 최소 입찰 단위보다 작습니다. 최소 " + (currentBid.getAmount() + auction.getMinBid()) + "원 이상 입찰해야 합니다.");
        }

        /*redisCommon.putInHash(key, "amount", request.getAmount());
        redisCommon.putInHash(key, "userUuid", request.getUserUuid());*/
        redisCommon.putAllInHash(key, Map.of("amount", request.getAmount(), "userUuid", request.getUserUuid()));

        // 입찰 처리 (새로 생성하거나 금액 갱신)
        Bid bid = Bid.createBid(auction, user, request.getAmount(), LocalDateTime.now());

        // 입찰 저장
        bidRepository.save(bid);

        // BidDto 변환 후 반환
        return BidCreateResponse.from(bid);
    }
}
