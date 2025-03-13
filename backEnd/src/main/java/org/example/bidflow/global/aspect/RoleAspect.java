package org.example.bidflow.global.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.bidflow.data.Role;
import org.example.bidflow.global.annotation.HasRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Aspect
@Component
public class RoleAspect {

    // @HasRole 어노테이션이 메서드나 클래스에 있으면 이 메서드 실행
    @Around("@annotation(hasRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, HasRole hasRole) throws Throwable {

        // 어노테이션에서 필요한 역할(Role)을 가져옴 (예: "ADMIN")
        Role requiredRole = hasRole.value();

        // 현재 메서드를 호출하는 사용자의 인증 정보를 SecurityContext 에서 가져옴
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 인증되지 않았거나, 로그인이 안 된 상태면 예외를 발생시킴 (401 UNAUTHORIZED)
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // 사용자가 요구된 역할을 가지고 있는지 체크 (예: ROLE_ADMIN 이 있는지 확인)
        boolean hasAuthority = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + requiredRole.name()));

        // 만약 사용자가 역할이 없다면 예외를 발생 (403 FORBIDDEN)
        if (!hasAuthority) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        log.info("권한 확인 성공: {}", requiredRole);
        return joinPoint.proceed();  // 권한이 있으면, 원래 메서드를 계속 진행하도록 허용
    }
}
