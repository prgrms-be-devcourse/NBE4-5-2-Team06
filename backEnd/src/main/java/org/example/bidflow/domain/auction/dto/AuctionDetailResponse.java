package org.example.bidflow.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.product.dto.ProductResponse;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionDetailResponse {

    private final Long auctionId;
    private final ProductResponse product;
    private final Integer startPrice;
    private final Integer currentBid;
    private final Integer minBid;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String status;

    // 엔티티를 DTO로 변환
    public static AuctionDetailResponse from(Auction auction, Integer amount) {
        return AuctionDetailResponse.builder()
                .auctionId(auction.getAuctionId())
                .product(ProductResponse.from(auction.getProduct()))      // product DTO 변환
                .startPrice(auction.getStartPrice())
                .currentBid(amount)
                .minBid(auction.getMinBid())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus().name())
                .build();
    }
}
