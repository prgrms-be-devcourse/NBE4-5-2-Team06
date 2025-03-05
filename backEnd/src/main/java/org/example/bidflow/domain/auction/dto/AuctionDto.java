package org.example.bidflow.domain.auction.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class AuctionDto {


    private Long auctionId;
    private String productName;
    private String imageUrl;
    private Integer winningBid;
    private Enum status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    // 생성자 추가
    public AuctionDto(Long auctionId, String productName, String imageUrl, Integer winningBid,
                      Enum status, LocalDateTime startTime, LocalDateTime endTime) {
        this.auctionId = auctionId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.winningBid = winningBid;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
