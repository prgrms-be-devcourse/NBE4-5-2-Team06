package org.example.bidflow.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.product.dto.ProductResponse;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionResponse {
    private AuctionData auction;
    private ProductResponse product;

    public static AuctionResponse of(Auction auction) {
        return AuctionResponse.builder()
                .auction(AuctionData.of(auction))
                .product(ProductResponse.from(auction.getProduct()))
                .build();
    }

    @Getter
    @Builder
    public static class AuctionData {
        private Long auctionId;
        private Long productId;
        private Integer startPrice;
        private Integer minBid;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;

        public static AuctionData of(Auction auction) {
            return AuctionData.builder()
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
}