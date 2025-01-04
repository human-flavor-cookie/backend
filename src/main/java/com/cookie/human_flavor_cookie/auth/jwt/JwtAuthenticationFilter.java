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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 토큰 받아오기
        String token = resolveToken(request);

        // 토큰이 유효하다면 유저 정보를 가져와 SecurityContext와 요청 속성에 설정
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmail(token);

            // SecurityContext에 인증 객체 저장
            JwtAuthentication authentication = new JwtAuthentication(email);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Member 객체를 조회하여 요청 속성에 추가
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            request.setAttribute("member", member);
        }

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
