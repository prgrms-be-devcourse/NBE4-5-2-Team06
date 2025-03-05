package org.example.bidflow.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.data.Role;
import org.example.bidflow.domain.user.dto.UserSignInRequest;
import org.example.bidflow.domain.user.dto.UserSignInResponse;
import org.example.bidflow.domain.user.dto.UserSignUpRequest;
import org.example.bidflow.domain.user.dto.UserSignUpResponse;
import org.example.bidflow.domain.user.entity.User;
import org.example.bidflow.domain.user.repository.UserRepository;
import org.example.bidflow.global.exception.ServiceException;
import org.example.bidflow.global.utils.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    public UserSignUpResponse signup(UserSignUpRequest request) {

        // 이메일 혹은 닉네임으로 존재하는 유저가 있는지 확인
        Optional<User> existingUser = userRepository.findByEmailOrNickname(request.getEmail(), request.getNickname());

        // 이메일 혹은 닉네임이 중복되는 경우 예외 처리
        if(existingUser.isPresent()) {
            throw new ServiceException(HttpStatus.CONFLICT.value() + "", "이미 사용 중인 이메일 또는 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .userUuid(System.currentTimeMillis() + "-" + UUID.randomUUID())
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        // 데이터베이스에 저장
        userRepository.save(user);

        return UserSignUpResponse.from(user);

    }

    public UserSignInResponse login(UserSignInRequest request) {

        // 이메일을 기준으로 사용자를 찾음 (존재하지 않으면 예외 발생)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ServiceException(HttpStatus.UNAUTHORIZED.value() + "", "이메일 또는 비밀번호가 일치하지 않습니다."));

        // 입력된 비밀번호가 저장된 비밀번호와 일치하는지 확인 (일치하지 않으면 예외 발생)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.value() + "", "이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 발행 시 포함할 사용자 정보 설정
        Map<String, Object> claims = new HashMap<>();
        claims.put("userUuid", user.getUserUuid());
        claims.put("nickname", user.getNickname());
        claims.put("role", user.getRole());

        // JWT 토큰 생성
        String token = jwtProvider.generateToken(claims);

        // 응답 객체 생성 및 반환
        return UserSignInResponse.from(user, token);
    }
}
