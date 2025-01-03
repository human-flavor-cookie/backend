package com.cookie.human_flavor_cookie.auth.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthentication extends AbstractAuthenticationToken {
    private final String email;

    public JwtAuthentication(String email) {
        super(null);
        this.email = email;
        setAuthenticated(true); // JWT로 인증된 사용자이므로 기본 true로 설정
    }

    @Override
    public Object getCredentials() {
        return null; // JWT는 credentials(자격 증명)이 필요하지 않음
    }

    @Override
    public Object getPrincipal() {
        return email; // 이메일을 Principal로 사용
    }

    public String getEmail() {
        return email;
    }
}

