package org.example.bidflow.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.bidflow.domain.user.entity.User;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPutRequest {

    private String profileImage;
    private String nickname;
    private String email;

    public static UserPutRequest from(User user) {
        return UserPutRequest.builder()
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
