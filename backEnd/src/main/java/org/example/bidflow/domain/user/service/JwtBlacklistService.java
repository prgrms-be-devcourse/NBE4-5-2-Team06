package org.example.bidflow.global.utils;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private final JwtProvider jwtProvider;

    // 블랙리스트에 추가 (로그아웃 처리)
    public void addToBlacklist(String token) {

        Claims claims = jwtProvider.parseClaims(token);
        long expirationTime = claims.getExpiration().getTime();
        long ttl = expirationTime - System.currentTimeMillis();
        // 만료 시간 계산 (하루)
        // 이 값은 Redis에 저장된 토큰이 남은 시간만큼 유효하도록 설정

        // 블랙리스트 확인 후 예외 발생
        if (isBlacklisted(token)) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.value() + "", "유효하지 않은 토큰입니다.");
        }

        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "logout", ttl, TimeUnit.MILLISECONDS);
        // Redis에 토큰을 "logout" 값과 함께 저장하며, TTL(Time-to-Live)을 밀리초 단위로 설정한다.
    }

    // 블랙리스트 여부 확인
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
        // 해당 token이 블랙리스트에 있는지 확인하는 메서드
    }

}
