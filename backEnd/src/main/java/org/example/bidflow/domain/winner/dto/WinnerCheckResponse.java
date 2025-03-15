package org.example.bidflow.domain.winner.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.winner.entity.Winner;

import java.time.LocalDateTime;

@Getter
@Builder
public class WinnerCheckResponse {
    private final Long auctionId;
    private final String productName;
    private final String description;
    private final Integer winningBid;
    private final LocalDateTime winTime;
    private String imageUrl;


    // Winner 엔티티를 낙찰자 조회 응답을 위한 DTO 로 변환
    public static WinnerCheckResponse from(Winner winner) {
        return WinnerCheckResponse.builder()
                .auctionId(winner.getAuction().getAuctionId())
                .productName(winner.getAuction().getProduct().getProductName())
                .description(winner.getAuction().getProduct().getDescription())
                .winningBid(winner.getWinningBid())
                .winTime(winner.getWinTime())
                .build();
    }
}
