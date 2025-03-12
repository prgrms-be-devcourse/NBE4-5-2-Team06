package org.example.bidflow.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.bidflow.domain.user.entity.User;

@Getter
@Builder
public class UserPutRequest {

    private String profileImage;
    private String nickname;
    private String email;

    public UserPutRequest from(User user) {
        return UserPutRequest.builder()
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
