package org.example.bidflow.domain.auction.dto;

import lombok.*;
import org.example.bidflow.domain.auction.entity.Auction;
import java.time.LocalDateTime;
import org.example.bidflow.domain.product.dto.ProductResponse;

@Getter
@Builder
public class AuctionResponse {

    private Long auctionId;
    private String productName;
    private String imageUrl;
    private Integer currentPrice;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    //  생성자
    public static AuctionResponse from(Long auctionId, String productName, String imageUrl, Integer currentPrice,
                                       String status, LocalDateTime startTime, LocalDateTime endTime) {
        return AuctionResponse.builder()
                .auctionId(auctionId)
                .productName(productName)
                .imageUrl(imageUrl)
                .currentPrice(currentPrice)
                .status(status)
                .startTime(startTime)
                .endTime(endTime).build();
    }
}

