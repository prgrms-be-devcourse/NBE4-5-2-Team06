package org.example.bidflow.domain.bid.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionBidRequest;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.auction.service.AuctionService;
import org.example.bidflow.domain.bid.dto.BidCreateResponse;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.bid.repository.BidRepository;
import org.example.bidflow.domain.user.entity.User;
import org.example.bidflow.domain.user.service.UserService;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BidService {

    private final AuctionService auctionService;
    private final UserService userService;
    private final BidRepository bidRepository;

    @Transactional
    public BidCreateResponse createBid(Long auctionId, AuctionBidRequest request) {
        // 경매 상태 검증
        Auction auction = auctionService.getAuctionWithValidation(auctionId);

        // 사용자 검증
        User user = userService.getUserByUuid(request.getUserUuid());

        // 기존 입찰 조회
        Bid existingBid = bidRepository.findByAuctionAndUser(auction, user).orElse(null);

        // 입찰 금액 검증
        validateBidAmount(existingBid, request.getAmount());

        // 입찰 처리 (새로 생성하거나 금액 갱신)
        Bid bid = processBid(existingBid, auction, user, request.getAmount());

        // 입찰 저장
        bidRepository.save(bid);

        // BidDto 변환 후 반환
        return BidCreateResponse.from(bid);
    }

    // 입찰 금액 검증 메서드
    private void validateBidAmount(Bid existingBid, Integer newAmount) {
        if (existingBid != null && existingBid.getAmount() >= newAmount) {
            throw new ServiceException("400-3", "새로운 입찰 금액은 기존 입찰 금액보다 커야 합니다.");
        }
    }

    // 입찰 처리 메서드 (새로 생성하거나 금액 갱신)
    private Bid processBid(Bid existingBid, Auction auction, User user, Integer amount) {
        if (existingBid == null) {
            // 새 입찰 생성
            return Bid.createBid(auction, user, amount, LocalDateTime.now());
        } else {
            // 기존 입찰 금액 갱신
            return existingBid.updateAmount(amount);
        }
    }
}
