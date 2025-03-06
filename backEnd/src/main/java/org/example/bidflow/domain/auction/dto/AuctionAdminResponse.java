package org.example.bidflow.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.auction.entity.Auction;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionAdminResponse {

    private Long auctionId;
    private String productName;
    private String imageUrl;
    private Integer currentPrice;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String nickname; // "FINISHED" 상태일 경우 포함
    private Integer winningBid; // "FINISHED" 상태일 경우 포함
    private LocalDateTime winTime; // "FINISHED" 상태일 경우 포함



    public static AuctionAdminResponse from(Auction auction) {
        AuctionAdminResponse.AuctionAdminResponseBuilder builder = AuctionAdminResponse.builder()
                .auctionId(auction.getAuctionId())
                .productName(auction.getProduct().getProductName())  // 상품명
                .imageUrl(auction.getProduct().getImageUrl())        // 이미지 URL
                .currentPrice(auction.getStartPrice()) //현재 가격
                .status(auction.getStatus().name())  // Enum 직접 사용
                .startTime(auction.getStartTime())   // 시작 시간
                .endTime(auction.getEndTime());      // 종료 시간

            //경매가 종료된 경우
        if ("FINISHED".equals(auction.getStatus().toString())) {
            // 낙찰자 정보 추가
            builder.nickname(auction.getWinner().getUser().getNickname()) // 경매 종료 시 낙찰자 닉네임
                    .winningBid(auction.getWinner().getWinningBid())  // 경매 종료 시 낙찰가
                    .winTime(auction.getWinner().getWinTime());      // 경매 종료 시 낙찰 시간
        }

        return builder.build();
    }

}
