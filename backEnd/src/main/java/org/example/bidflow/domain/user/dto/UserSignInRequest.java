package org.example.bidflow.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserSignInRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.") // 공백일 경우 검증 실패
    @Email(message = "이메일 형식이 올바르지 않습니다.") // 이메일 형식 검증
    private final String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.") // 공백일 경우 검증 실패
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.") // 최소 길이 검증
    private final String password;
}