package org.example.bidflow.domain.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.global.app.RedisCommon;
import org.example.bidflow.global.exception.ServiceException;
import org.example.bidflow.global.utils.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisCommon redisCommon;
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private final JwtProvider jwtProvider;

    // 블랙리스트에 추가 (로그아웃 처리)
    public void addToBlacklist(String token) {

        Claims claims;

        try {
            claims = jwtProvider.parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.value() + "", "만료된 토큰입니다.");
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value() + "", "유효하지 않은 토큰입니다.");
        }

        long expirationTime = claims.getExpiration().getTime();
        long ttl = expirationTime - System.currentTimeMillis();
        // 만료 시간 계산 (하루)
        // 이 값은 Redis에 저장된 토큰이 남은 시간만큼 유효하도록 설정

        long ttlSeconds = ttl / 1000;

        String key = getKey(token);
        redisCommon.putInHash(key, "blacklisted", "true");
        redisCommon.setExpireAt(key, LocalDateTime.now().plusSeconds(ttlSeconds));
    }

    public static String getKey(String token) {
        return BLACKLIST_PREFIX + token;
    }

    // 블랙리스트 여부 확인
    public boolean isBlacklisted(String token) {
        String key = getKey(token);
        return "true".equals(redisCommon.getFromHash(key,"blacklisted",String.class));
    }

}
