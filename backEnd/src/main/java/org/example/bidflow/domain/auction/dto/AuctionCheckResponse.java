package org.example.bidflow.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.auction.entity.Auction;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionCheckResponse {

    private Long auctionId;
    private String productName;
    private String imageUrl;
    private Integer currentBid;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;



       //Entity -> DTO 변환
        public static AuctionCheckResponse from(Auction auction, Integer currentBid) {
            return AuctionCheckResponse.builder()
                    .auctionId(auction.getAuctionId())
                    .productName(auction.getProduct().getProductName())  // Product에서 상품명 가져오기
                    .imageUrl(auction.getProduct().getImageUrl())      // Product에서 이미지 URL 가져오기
                    .currentBid(currentBid)  // 현재 가격 가져오기
                    .status(auction.getStatus().toString())  // 경매 상태 Enum을 String으로 변환하기
                    .startTime(auction.getStartTime())  //경매 시작 시간
                    .endTime(auction.getEndTime())  //경매 종료 시간
                    .build();
        }

    }

