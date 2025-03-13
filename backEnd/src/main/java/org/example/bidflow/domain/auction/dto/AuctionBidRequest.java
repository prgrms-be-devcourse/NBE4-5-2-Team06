package org.example.bidflow.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.bidflow.domain.auction.service.AuctionService;

@Getter
@Builder
public class AuctionBidRequest {
    private final Long auctionId;
    private final Integer amount;
    private final String token;
}
