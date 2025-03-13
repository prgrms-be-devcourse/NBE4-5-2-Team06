package org.example.bidflow.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.data.Role;
import org.example.bidflow.domain.user.dto.*;
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


    public  UserCheckRequest getUserCheck(String userUUID) {
        User user = getUserByUUID(userUUID); //userUUID로 조회
        return UserCheckRequest.from(user); //DTO로 반환한다
    }
//    public User getUserUserId(Long auctionId) {
//
//        // 경매 조회
//        User user = userRepository.findByUserId(auctionId)
//                .orElseThrow(() -> new ServiceException("400-1", "사용자가 존재 하지 않습니다."));
//
//        return user;
//    }


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
                .userUUID(System.currentTimeMillis() + "-" + UUID.randomUUID())
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        // 데이터베이스에 저장
        userRepository.save(user);

        return UserSignUpResponse.from(user);
    }

    // UUID를 기반으로 유저 검증
    public User getUserByUUID(String userUUID) {
        return userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new ServiceException("400", "사용자가 존재하지 않습니다."));
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
        claims.put("userUUID", user.getUserUUID());
        claims.put("nickname", user.getNickname());
        claims.put("role", user.getRole());

        // JWT 토큰 생성
        String token = jwtProvider.generateToken(claims);

        // 응답 객체 생성 및 반환
        return UserSignInResponse.from(user, token);
    }

    public UserPutRequest updateUser(String userUUID, UserPutRequest request) {
        User user = getUserByUuid(userUUID); //사용자 조회

        user.setProfileImage(request.getProfileImage()); //변경내용
        user.setNickname(request.getNickname());    //변경 내용
        user.setEmail(request.getEmail()); //변경 내용

        User updatedUser = userRepository.save(user); //변경 내용을 저장
        return UserPutRequest.from(updatedUser); //변경 내용이 저장된걸 DTO로 변환
    }

}

