package com.cookie.human_flavor_cookie.member.controller;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.member.dto.CreateFriendRequestDto;
import com.cookie.human_flavor_cookie.member.dto.RespondFriendRequestDto;
import com.cookie.human_flavor_cookie.member.entity.Member;
import com.cookie.human_flavor_cookie.member.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend-requests")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    /**
     * 친구 요청 생성
     */
    @PostMapping
    public ResponseEntity<String> createFriendRequest(
            @LoginUser Member member,
            @Validated @RequestBody CreateFriendRequestDto dto
    ) {
        Long currentUserId = member.getId();

        friendRequestService.createFriendRequest(dto, currentUserId);
        return ResponseEntity.ok("친구 요청을 전송했습니다.");
    }

    /**
     * 친구 요청 응답 (수락/거절)
     */
    @PatchMapping("/{friendRequestId}")
    public ResponseEntity<String> respondToFriendRequest(
            @LoginUser Member member,
            @PathVariable Long friendRequestId,
            @Validated @RequestBody RespondFriendRequestDto dto

    ) {
        // 실제 구현에서는 로그인한 사용자의 ID를 가져와야 함
        Long currentUserId = member.getId();

        friendRequestService.respondFriendRequest(friendRequestId, currentUserId, dto.getAction());
        return ResponseEntity.ok("친구 요청을 처리했습니다.");
    }
}
