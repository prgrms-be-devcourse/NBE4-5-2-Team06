package org.example.bidflow.domain.bid.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.user.dto.UserSignUpRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BidDto{

    private Long bidId;            // 입찰 ID
    private UserSignUpRequest user; // 사용자 정보 (UserSignUpRequest로 변경)
    private Integer amount;        // 입찰 금액
    private LocalDateTime bidTime; // 입찰 시간

//    // Bid 엔티티를 BidDto로 변환하는 메서드
//    public static BidDto from(Bid bid) {
//        // UserSignUpRequest는 사용자 정보를 담고 있지만,
//        // Bid 엔티티에서 관련 사용자의 정보를 가져와서 변환해야 합니다.
//        // 예시로, 사용자의 email, password, nickname 등을 포함시킬 수 있습니다.
//        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
//                .email(bid.getUser().getEmail())  // 예시로 이메일 가져오기
//                .password(bid.getUser().getPassword())  // 예시로 비밀번호 가져오기
//                .nickname(bid.getUser().getNickname())  // 예시로 닉네임 가져오기
//                .build();
//
//        return new BidDto(
//                bid.getBidId(),
//                userSignUpRequest,
//                bid.getAmount(),
//                bid.getBidTime()
//        );
//    }
}
