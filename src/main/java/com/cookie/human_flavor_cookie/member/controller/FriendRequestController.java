package com.cookie.human_flavor_cookie.member.controller;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.member.dto.CreateFriendRequestDto;
import com.cookie.human_flavor_cookie.member.dto.PendingRequestDto;
import com.cookie.human_flavor_cookie.member.dto.RespondFriendRequestDto;
import com.cookie.human_flavor_cookie.member.entity.Member;
import com.cookie.human_flavor_cookie.member.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend-requests")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    /**
     * 친구 요청 생성
     */
    @PostMapping
    public ResponseEntity<?> createFriendRequest(
            @LoginUser Member member,
            @Validated @RequestBody CreateFriendRequestDto dto
    ) {
        Long currentUserId = member.getId();
        friendRequestService.createFriendRequest(dto, currentUserId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "친구 요청을 전송했습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 요청 응답 (수락/거절)
     */
    @PostMapping("/respond")
    public ResponseEntity<?> respondToFriendRequest(
            @LoginUser Member member,
            @Validated @RequestBody RespondFriendRequestDto dto
    ) {
        // 실제로는 SecurityContextHolder, JWT 등을 통해 로그인된 사용자 ID를 가져와야 함
        Long currentUserId = member.getId(); // 예시로 1L

        friendRequestService.respondFriendRequest(dto, currentUserId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "친구 요청을 처리했습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/received-pending")
    public ResponseEntity<List<PendingRequestDto>> getMyPendingRequests(@LoginUser Member member) {
        // 실제로는 SecurityContext/JWT에서 로그인된 유저 ID를 가져와야 함
        Long currentUserId = member.getId(); // 예시용

        List<PendingRequestDto> pendingRequests =
                friendRequestService.getPendingRequestsForUser(currentUserId);

        return ResponseEntity.ok(pendingRequests);
    }

}
