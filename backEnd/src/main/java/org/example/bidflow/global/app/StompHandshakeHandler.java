package org.example.bidflow.global.app;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bidflow.global.utils.JwtProvider;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandshakeHandler implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;

    // Handshake 전 처리
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            log.error("WebSocket 요청이 HTTP 요청이 아닙니다.");
            throw new IllegalArgumentException("WebSocket 요청이 HTTP 요청이 아닙니다.");
        }

        HttpServletRequest httpRequest = servletRequest.getServletRequest();
        String token = extractToken(httpRequest);

        // WebSocket 세션 속성 저장
        attributes.put("userUUID", jwtProvider.parseUserUUID(token));
        attributes.put("nickname", jwtProvider.parseNickname(token));

        log.info("WebSocket Handshake 성공: userUUID={}, nickname={}", jwtProvider.parseUserUUID(token), jwtProvider.parseNickname(token));
        return true;
    }

    // Handshake 후 처리
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception){
        // 추가 로직 필요 시 사용.
    }

    // Jwt 토큰을 추출하는 메서드
    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null) {
            token = request.getParameter("token"); // SockJS 대응
        }

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // "Bearer " 제거
        }

        return token;
    }
}
