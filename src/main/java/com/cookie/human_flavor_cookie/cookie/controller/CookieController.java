package com.cookie.human_flavor_cookie.cookie.controller;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.cookie.dto.AssignCookieRequestDto;
import com.cookie.human_flavor_cookie.cookie.dto.UpdateCookieDistanceRequestDto;
import com.cookie.human_flavor_cookie.cookie.dto.UserCookieResponseDto;
import com.cookie.human_flavor_cookie.cookie.entity.UserCookie;
import com.cookie.human_flavor_cookie.cookie.service.CookieService;
import com.cookie.human_flavor_cookie.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<UserCookieResponseDto>> getCookiesForUser(@LoginUser Member member) {
        List<UserCookie> userCookies = cookieService.getCookiesForUser(member.getId());

        // 엔티티를 DTO로 변환
        List<UserCookieResponseDto> responseDtos = userCookies.stream()
                .map(userCookie -> UserCookieResponseDto.builder()
                        .cookieId(userCookie.getCookie().getCookieId())
                        .cookieName(userCookie.getCookie().getCookieName())
                        .isOwned(userCookie.isOwned())
                        .accumulatedDistance(userCookie.getAccumulatedDistance())
                        .build())
                .toList();

        return ResponseEntity.ok(responseDtos);
    }


    @PatchMapping("/update-distance")
    public ResponseEntity<String> updateCookieDistance(
            @RequestBody UpdateCookieDistanceRequestDto requestDto,
            @LoginUser Member member) {
        cookieService.updateCookieDistance(member.getId(), requestDto.getCookieId(), requestDto.getDistance());
        return ResponseEntity.ok("Cookie distance updated successfully");
    }

}
