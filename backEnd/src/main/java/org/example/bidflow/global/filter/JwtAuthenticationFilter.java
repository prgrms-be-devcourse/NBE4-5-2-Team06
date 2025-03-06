package org.example.bidflow.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.global.utils.JwtBlacklistService;
import org.example.bidflow.global.utils.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtBlacklistService jwtBlacklistService; // 블랙리스트 서비스

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization"); // 요청 헤더에서 "Authorization" 값을 가져옴

        if (token != null && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");

            // 블랙리스트에 있는 토큰인지 확인
            if (jwtBlacklistService.isBlacklisted(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 상태 반환
                response.getWriter().write("로그아웃된 토큰입니다.");
                return; // 차단된 토큰이므로 요청 중단
            }

            // 블랙리스트에 없는 경우에만 JWT 검증 진행
            jwtProvider.validateToken(token);
        }

        chain.doFilter(request, response);  // 필터가 끝난 후 다음 필터나 서블릿으로 요청을 넘김
    }
}
