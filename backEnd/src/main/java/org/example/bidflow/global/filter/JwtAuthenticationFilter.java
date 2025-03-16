package org.example.bidflow.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.user.service.JwtBlacklistService;
import org.example.bidflow.global.exception.ServiceException;
import org.example.bidflow.global.utils.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtBlacklistService jwtBlacklistService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {

                // 블랙리스트 확인
                if (jwtBlacklistService.isBlacklisted(token)) {
                    throw new ServiceException(HttpStatus.UNAUTHORIZED.value() + "", "로그아웃한 토큰으로 접근할 수 없습니다.");
                }

                // 토큰 유효성 검사
                if (jwtProvider.validateToken(token)) {
                    // 토큰에서 필요한 정보 추출
                    String username = jwtProvider.getUsername(token);
                    String role = jwtProvider.parseRole(token);  // 👉 role 추출
                    System.out.println("Extracted Role: " + role);

                    // 직접 UserDetails 생성
                    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                            .username(username)
                            .password("") // 비밀번호는 인증에 필요하지 않음
                            .authorities(new SimpleGrantedAuthority(role)) // 권한 설정
                            .build();

                    System.out.println("Authorities: " + userDetails.getAuthorities());

                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // SecurityContextHolder에 인증 정보 등록
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }

        filterChain.doFilter(request, response);
    }

    // 헤더에서 토큰 추출 (Authorization: Bearer <token>)
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

