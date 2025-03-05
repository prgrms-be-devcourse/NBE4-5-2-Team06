package org.example.bidflow.domain.auction.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionCheckResponse {

    private Long auctionId;
    private String productName;
    private String imageUrl;
    private Integer currentPrice;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    //  생성자
    public static AuctionCheckResponse from(Long auctionId, String productName, String imageUrl, Integer currentPrice,
                                       String status, LocalDateTime startTime, LocalDateTime endTime) {
        return AuctionCheckResponse.builder()
                .auctionId(auctionId)
                .productName(productName)
                .imageUrl(imageUrl)
                .currentPrice(currentPrice)
                .status(status)
                .startTime(startTime)
                .endTime(endTime).build();
    }
}

