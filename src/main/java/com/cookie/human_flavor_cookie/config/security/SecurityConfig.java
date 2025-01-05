package com.cookie.human_flavor_cookie.config.security;

import com.cookie.human_flavor_cookie.auth.jwt.JwtAuthenticationFilter;
import com.cookie.human_flavor_cookie.kakao.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**", "/oauth/kakao/callback", "/user").permitAll() // 인증 없이 허용
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/user", true) // 인증 성공 후 이동할 URL
                        .failureUrl("/login?error")       // 인증 실패 시 이동할 URL
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService())) // 사용자 정보 처리
                );

        return http.build();
    }
    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService();
    }
}
