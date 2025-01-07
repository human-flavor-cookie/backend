package com.cookie.human_flavor_cookie.member.service;

import com.cookie.human_flavor_cookie.member.dto.CreateFriendRequestDto;
import com.cookie.human_flavor_cookie.member.entity.FriendRequest;
import com.cookie.human_flavor_cookie.member.entity.FriendRequestStatus;
import com.cookie.human_flavor_cookie.member.entity.Member;
import com.cookie.human_flavor_cookie.member.repository.FriendRequestRepository;
import com.cookie.human_flavor_cookie.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendRequestService {

    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;

    /**
     * 1) 친구 요청 생성
     * - 중복 요청 방지 (이미 PENDING/ACCEPTED 상태 있으면 X)
     * - 자기 자신에게 요청 방지
     * - 상대방 이메일 존재 여부
     */
    public void createFriendRequest(CreateFriendRequestDto dto, Long currentUserId) {
        Member requester = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자가 존재하지 않습니다."));

        Member receiver = memberRepository.findByEmail(dto.getReceiverEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다."));

        // 자기 자신에게 요청 불가
        if (requester.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("본인에게는 친구 요청을 보낼 수 없습니다.");
        }

        // 이미 PENDING or ACCEPTED 되어있는지 확인
        boolean exists = friendRequestRepository.existsByRequesterIdAndReceiverIdAndStatusIn(
                requester.getId(),
                receiver.getId(),
                List.of(FriendRequestStatus.PENDING, FriendRequestStatus.ACCEPTED)
        );
        if (exists) {
            throw new IllegalArgumentException("이미 친구 요청이 진행 중이거나 이미 친구입니다.");
        }

        // FriendRequest 생성
        FriendRequest friendRequest = FriendRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        friendRequestRepository.save(friendRequest);
    }

    /**
     * 2) 친구 요청 응답 (수락/거절)
     */
    public void respondFriendRequest(Long friendRequestId, Long currentUserId, String action) {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));

        // 현재 사용자가 수신자인지 검증
        if (!friendRequest.getReceiver().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("해당 요청을 응답할 권한이 없습니다.");
        }

        // PENDING 상태인지 확인
        if (friendRequest.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        // 액션에 따라 상태 변경
        if ("ACCEPT".equalsIgnoreCase(action)) {
            friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
        } else if ("REJECT".equalsIgnoreCase(action)) {
            friendRequest.setStatus(FriendRequestStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("올바르지 않은 action 값입니다. (ACCEPT 또는 REJECT)");
        }

        friendRequestRepository.save(friendRequest);
    }
}
