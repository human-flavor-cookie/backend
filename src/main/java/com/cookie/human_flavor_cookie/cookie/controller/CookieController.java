package com.cookie.human_flavor_cookie.cookie.controller;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.cookie.dto.*;
import com.cookie.human_flavor_cookie.cookie.entity.UserCookie;
import com.cookie.human_flavor_cookie.cookie.service.CookieService;
import com.cookie.human_flavor_cookie.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cookie")
public class CookieController {
    private final CookieService cookieService;
    @PostMapping("/assign")
    public ResponseEntity<String> assignCookieToUser(
            @LoginUser Member member,
            @RequestBody AssignCookieRequestDto requestDto
            ) {
        System.out.println("Checkpoint");
        cookieService.assignCookieToUser(member, requestDto.getCookieId());
        System.out.println("Received cookieId: " + requestDto.getCookieId());
        return ResponseEntity.ok("Cookie assigned successfully");
    }
    @GetMapping("/list")
    public ResponseEntity<List<UserCookieResponseDto>> getUserCookies(@LoginUser Member member) {
        // 서비스 호출
        List<UserCookieResponseDto> cookies = cookieService.getUserCookies(member);
        return ResponseEntity.ok(cookies);
    }
    @PatchMapping("/update-distance")
    public ResponseEntity<String> updateCookieDistance(
            @RequestBody UpdateCookieDistanceRequestDto requestDto,
            @LoginUser Member member) {
        cookieService.updateCookieDistance(member.getId(), requestDto.getCookieId(), requestDto.getDistance());
        return ResponseEntity.ok("Cookie distance updated successfully");
    }
    @PatchMapping("/change")
    public ResponseEntity<?> changeCurrentCookie(
            @LoginUser Member member,
            @RequestBody ChangeCookieRequestDto requestDto) {
        cookieService.changeCurrentCookie(member, requestDto.getCookieId());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cookie changed successfully");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseCookie(
            @LoginUser Member member,
            @RequestBody PurchaseCookieRequestDto requestDto) {
        cookieService.purchaseCookie(member, requestDto.getCookieId());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cookie purchased successfully.");
        return ResponseEntity.ok(response);
    }
}
