package com.cookie.human_flavor_cookie.auth.jwt;

import com.cookie.human_flavor_cookie.member.entity.Member;
import com.cookie.human_flavor_cookie.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository; // MemberRepository 추가

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // /oauth/kakao/callback 및 /oauth2/** 경로는 JWT 필터를 우회
        if (requestURI.startsWith("/oauth/kakao/callback") || requestURI.startsWith("/oauth2/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 검증 로직
        String token = resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmail(token);

            // SecurityContext에 인증 객체 저장
            JwtAuthentication authentication = new JwtAuthentication(email);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            System.err.println("JWT validation failed or token is missing");
        }

        System.out.println("Extracted token: " + token);
        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
