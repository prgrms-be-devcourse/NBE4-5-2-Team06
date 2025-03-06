package org.example.bidflow.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.user.dto.UserSignInRequest;
import org.example.bidflow.domain.user.dto.UserSignInResponse;
import org.example.bidflow.domain.user.dto.UserSignUpRequest;
import org.example.bidflow.domain.user.dto.UserSignUpResponse;
import org.example.bidflow.domain.user.entity.User;
import org.example.bidflow.domain.user.service.UserService;
import org.example.bidflow.global.dto.RsData;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<RsData<UserSignUpResponse>> signup(@Valid @RequestBody UserSignUpRequest request) {

        UserSignUpResponse response = userService.signup(request);

        // 회원가입 성공 시 응답 데이터 생성 (201: Created)
        RsData<UserSignUpResponse> rsData  = new RsData<>("201", "회원가입이 완료되었습니다.", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(rsData);
    }


    @PostMapping("/login")
    public ResponseEntity<RsData<UserSignInResponse>> signin(@Valid @RequestBody UserSignInRequest request) {

        // 로그인 서비스 호출
        UserSignInResponse response = userService.login(request);

        // 성공 응답 생성
        RsData<UserSignInResponse> rsData  = new RsData<>("200", "로그인이 완료되었습니다.", response);

        // HTTP 200 OK 응답 반환
        return ResponseEntity.ok(rsData);
    }
}
