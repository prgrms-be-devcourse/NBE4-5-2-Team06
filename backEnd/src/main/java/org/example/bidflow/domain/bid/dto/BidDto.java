package org.example.bidflow.domain.bid.dto;

import lombok.*;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.user.dto.UserSignUpRequest;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidDto{

    private Long bidId;            // 입찰 ID
    private UserSignUpRequest user; // 사용자 정보
    private Integer amount;        // 입찰 금액
    private LocalDateTime bidTime; // 입찰 시간

}
