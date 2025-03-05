package org.example.bidflow.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.auction.entity.Auction;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionCreateDataResponse {
    private final Long auctionId;
    private final Long productId;
    private final Integer startPrice;
    private final Integer minBid;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String status;

    // 경매 등록 정보 DTO
    // Auction 엔티티를 DTO로 변환
    public static AuctionCreateDataResponse from(Auction auction) {
        return AuctionCreateDataResponse.builder()
                .auctionId(auction.getAuctionId())
                .productId(auction.getProduct().getProductId())
                .startPrice(auction.getStartPrice())
                .minBid(auction.getMinBid())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus().name())
                .build();
    }
}