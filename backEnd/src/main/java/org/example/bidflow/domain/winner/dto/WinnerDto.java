package org.example.bidflow.domain.winner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.bidflow.domain.auction.dto.AuctionResponse;
import org.example.bidflow.domain.user.dto.UserSignUpRequest;
import org.example.bidflow.domain.winner.entity.Winner;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WinnerDto {

    private Long winnerId;        // 낙찰자 ID
    private UserSignUpRequest user;  // 사용자 정보
    private AuctionResponse auction;   // 경매 정보
    private Integer winningBid;   // 낙찰 금액
    private LocalDateTime winTime; // 낙찰 시간

}
