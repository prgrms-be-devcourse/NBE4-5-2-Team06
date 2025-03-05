package org.example.bidflow.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.product.dto.ProductResponse;

@Getter
@Builder
public class AuctionResponse {
    private AuctionDataResponse auction;
    private ProductResponse product;

    // 경매 등록 응답을 위한 DTO
    public static AuctionResponse from(Auction auction) {
        return AuctionResponse.builder()
                .auction(AuctionDataResponse.from(auction))
                .product(ProductResponse.from(auction.getProduct()))
                .build();
    }
}