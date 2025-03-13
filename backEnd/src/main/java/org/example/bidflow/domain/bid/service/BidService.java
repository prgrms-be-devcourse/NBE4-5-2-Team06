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
import org.example.bidflow.global.utils.JwtProvider;
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
    private final JwtProvider jwtProvider;

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

        // 유저 및 경매 정보 가져오기
        String userUUID = jwtProvider.parseUserUuid(request.getToken());    // jwt토큰 파싱해 유저UUID 가져오기
        User user = userService.getUserByUuid(userUUID);
        Auction auction = auctionService.getAuctionWithValidation(auctionId);

        // 경매 시간 검증
        validateAuctionTime(now, auction);

        // Redis에서 현재 최고가 조회
        Integer amount = redisCommon.getFromHash(hashKey, "amount", Integer.class);
        int currentBidAmount = (amount != null) ? amount : auction.getStartPrice();     // DB 테스트를 위한 redis에 없으면 시작가로 설정

        // 최소 입찰 단위 검증
        validateBidAmount(request.getAmount(), currentBidAmount, auction.getMinBid());

        // Redis에 입찰 정보 갱신
        redisCommon.putInHash(hashKey, "amount", request.getAmount());
        redisCommon.putInHash(hashKey, "userUuid", userUUID);

        // DB 저장 (낙찰용 로그로 남김)
        Bid bid = Bid.createBid(auction, user, request.getAmount(), LocalDateTime.now());
        bidRepository.save(bid);

        return BidCreateResponse.from(bid);
    }

    // 0 -> o 105,0000

    // 경매 시간 유효성 검증
    private void validateAuctionTime(LocalDateTime now,  Auction auction) {
        if(now.isBefore(auction.getStartTime())){
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(), "경매가 시작 전입니다.");
        }else if(now.isAfter(auction.getEndTime())){
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(), "경매가 종료 되었습니다.");
        }
    }


    /** 입찰 금액 유효성 검증
     * @param newAmount         받아온 입찰 금액
     * @param currentAmount     최근 조희 금액
     * @param minBidAmount      최소 입찰 금액 단위
     */
    private void validateBidAmount(Integer newAmount, Integer currentAmount, Integer minBidAmount) {
        if(newAmount <= currentAmount) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(), "입찰 금액이 현재 최고가보다 낮습니다.");
        }

        if(newAmount < (currentAmount + minBidAmount)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.toString(),
                    "입찰 금액이 최소 입찰 단위보다 작습니다. 최소 " + (currentAmount + minBidAmount) + "원 이상 입찰해야 합니다.");
        }
    }
}