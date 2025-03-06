package org.example.bidflow.domain.auction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.bidflow.domain.auction.service.AuctionService;

@Getter
@Builder
public class AuctionBidRequest {
    private final String userUuid;
    private final Integer amount;
}
