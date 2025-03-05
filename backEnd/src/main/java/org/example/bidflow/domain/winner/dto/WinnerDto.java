package org.example.bidflow.domain.winner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.bidflow.domain.auction.dto.AuctionDto;
import org.example.bidflow.domain.user.dto.UserSignUpRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WinnerDto {

    private Long winnerId;        // 낙찰자 ID
    private UserSignUpRequest user;  // 사용자 정보
    private AuctionDto auction;   // 경매 정보
    private Integer winningBid;   // 낙찰 금액
    private LocalDateTime winTime; // 낙찰 시간

//    // Winner 엔티티를 WinnerDto로 변환하는 메서드
//    public static WinnerDto from(Winner winner) {
//        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
//                .email(winner.getUser().getEmail())
//                .password(winner.getUser().getPassword())
//                .nickname(winner.getUser().getNickname())
//                .build();
//
//        AuctionDto auctionDto = AuctionDto.from(winner.getAuction()); // 경매 정보를 AuctionDto로 변환
//
//        return new WinnerDto(
//                winner.getUsrId(),
//                userSignUpRequest,
//                auctionDto,
//                winner.getWinningBid(),
//                winner.getWinTime()
//        );
//    }
}
