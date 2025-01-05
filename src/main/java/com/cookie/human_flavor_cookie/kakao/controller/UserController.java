package com.cookie.human_flavor_cookie.kakao.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {
    @GetMapping("/user")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new IllegalStateException("사용자가 인증되지 않았습니다.");
        }
        System.out.println("사용자 정보: " + oAuth2User.getAttributes());
        return oAuth2User.getAttributes();
    }
}
