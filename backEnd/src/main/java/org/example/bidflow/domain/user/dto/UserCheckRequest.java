package org.example.bidflow.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.user.entity.User;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserCheckRequest {

    private final String userUUID;
    private final String nickname;
    private final String email;
    private final LocalDateTime createdAt;
    private final String profileImage; //프로필 이미지

    public static UserCheckRequest from(User user) {
        return UserCheckRequest.builder()
                .userUUID(user.getUserUUID())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .createdAt(user.getCreatedDate())
                .profileImage(user.getProfileImage())
                .build();
    }
}

