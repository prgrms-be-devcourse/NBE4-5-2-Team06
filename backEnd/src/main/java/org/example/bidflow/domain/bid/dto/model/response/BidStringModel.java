package org.example.bidflow.domain.bid.dto.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BidStringModel {
    private Long auctionId;
    private String userUuid;
    private Integer amount;
}
