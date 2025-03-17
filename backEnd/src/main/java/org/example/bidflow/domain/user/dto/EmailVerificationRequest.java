package org.example.bidflow.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.") // 공백일 경우 검증 실패
    @Email(message = "이메일 형식이 올바르지 않습니다.") // 이메일 형식 검증
    private String email;
    private String code;
}
