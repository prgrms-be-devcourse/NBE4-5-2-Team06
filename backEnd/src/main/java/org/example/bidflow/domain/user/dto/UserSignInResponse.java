package org.example.bidflow.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.user.entity.User;

@Builder
@Getter
public class UserSignInResponse {


    private final String token; // 발급된 JWT 토큰
    private final String userUUID; // 사용자 고유 식별자
    private final String nickname; // 사용자 닉네임

    // User 객체와 토큰을 받아 응답 객체를 생성하는 정적 메서드
    public static UserSignInResponse from(User user, String token) {
        return UserSignInResponse.builder()
                .userUUID(user.getUserUUID())
                .token(token)
                .nickname(user.getNickname())
                .build();
    }
}