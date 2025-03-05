package org.example.bidflow.global.utils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private static final String SECRET_KEY = "ff124f1-51e8-775g-66ru-eer8e7nteb2e";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24시간 (밀리초 단위)

    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // HMAC-SHA 기반 키 생성


    // JWT 토큰을 생성하는 메서드
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims) // 사용자 정보 포함
                .setIssuedAt(new Date()) // 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명 알고리즘 적용
                .compact(); // 최종적으로 문자열로 변환하여 반환
    }
}
