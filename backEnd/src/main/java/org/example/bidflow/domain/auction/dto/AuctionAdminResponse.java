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
    private Integer startPrice;
    private Integer currentPrice;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String nickname; // "FINISHED" 상태일 경우 포함
    private Integer winningBid; // "FINISHED" 상태일 경우 포함
    private LocalDateTime winTime; // "FINISHED" 상태일 경우 포함

    public static AuctionAdminResponse from(Auction auction, Integer currentPrice) {
        AuctionAdminResponse.AuctionAdminResponseBuilder builder = AuctionAdminResponse.builder()
                .auctionId(auction.getAuctionId())
                .productName(auction.getProduct().getProductName())  // 상품명
                .imageUrl(auction.getProduct().getImageUrl())        // 이미지 URL
                .startPrice(auction.getStartPrice())    // 시작 가격
                .currentPrice(currentPrice) // 현재 가격
                .status(auction.getStatus().name())  // Enum 직접 사용
                .startTime(auction.getStartTime())   // 시작 시간
                .endTime(auction.getEndTime());      // 종료 시간

        // 🔥 경매가 종료되었을 때, 낙찰자 정보가 없을 수도 있음 → `null` 체크 추가
        if ("FINISHED".equals(auction.getStatus().toString()) && auction.getWinner() != null) {
            builder.nickname(auction.getWinner().getUser() != null ? auction.getWinner().getUser().getNickname() : "없음") // 낙찰자 닉네임 (없으면 "없음")
                    .winningBid(auction.getWinner().getWinningBid() != null ? auction.getWinner().getWinningBid() : 0)  // 낙찰가 (없으면 0)
                    .winTime(auction.getWinner().getWinTime() != null ? auction.getWinner().getWinTime() : null);  // 낙찰 시간 (없으면 `null`)
        } else {
            // 낙찰자 없이 종료된 경우 기본값 설정
            builder.nickname("없음")
                    .winningBid(0)
                    .winTime(null);
        }

        // 프론트 단에서 피니쉬로 바뀌었을 때(메인 페이지를 접속할 때) end포인트로 요청을 보내서 auction의 status를 finish로 바꾸자
        // 관리자 페이지에 있는 관리자가 있다고 가정
        // 13시 - 종료시간 13시1~2분에

        return builder.build();
    }

}
