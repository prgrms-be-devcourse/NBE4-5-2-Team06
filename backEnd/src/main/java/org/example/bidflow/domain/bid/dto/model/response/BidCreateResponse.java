package org.example.bidflow.domain.bid.dto.model.response;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.bid.entity.Bid;

import java.time.LocalDateTime;

@Getter
@Builder
public class BidCreateResponse {

    private final Long auctionId;
    private final String userUuid;
    private final String  title;
    private final Integer bidAmount;
    private final LocalDateTime bidTime;

    // Bid 엔터티에서 BidDto로 변환
    public static BidCreateResponse from(Bid bid) {
        Auction auction = bid.getAuction();
        return BidCreateResponse.builder()
                .auctionId(auction.getAuctionId())
                .userUuid(bid.getUser().getUserUuid())
                .title(auction.getProduct().getProductName())
                .bidAmount(bid.getAmount())
                .bidTime(bid.getBidTime())
                .build();
    }
}
