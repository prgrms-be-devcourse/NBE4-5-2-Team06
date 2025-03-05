package org.example.bidflow.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.product.dto.ProductResponse;

@Getter
@Builder
public class AuctionCreateResponse {
    private AuctionCreateDataResponse auction;
    private ProductResponse product;

    // 경매 등록 응답을 위한 DTO
    public static AuctionCreateResponse from(Auction auction) {
        return AuctionCreateResponse.builder()
                .auction(AuctionCreateDataResponse.from(auction))
                .product(ProductResponse.from(auction.getProduct()))
                .build();
    }
}