package org.example.bidflow.domain.auction.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionRequest {
    private Integer startPrice;
    private Integer minBid;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @NotBlank(message = "상품 이름은 필수 입력 항목입니다.")
    private String productName;

    private String imageUrl;
    private String description;
}
