package org.example.bidflow.domain.bid.dto.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidInfo {
    private Integer amount;
    private String userUuid;
}