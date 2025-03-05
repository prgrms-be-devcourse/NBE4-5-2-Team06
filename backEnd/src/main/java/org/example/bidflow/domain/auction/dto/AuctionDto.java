package org.example.bidflow.domain.auction.dto;

import lombok.*;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.bid.dto.BidDto;
import org.example.bidflow.domain.product.dto.ProductDto;
import org.example.bidflow.domain.winner.dto.WinnerDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionDto {

    private Long auctionId;
    private String productName;
    private String imageUrl;
    private AuctionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    public static AuctionDto from(Auction auction) {
        return AuctionDto.builder()
                .auctionId(auction.getAuctionId())
                .productName(auction.getProduct().getProductName())
                .imageUrl(auction.getProduct().getImageUrl())
                .status(auction.getStatus())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .build();
    }
}
