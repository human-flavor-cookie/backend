package com.cookie.human_flavor_cookie.member.repository;

import com.cookie.human_flavor_cookie.member.entity.FriendRequest;
import com.cookie.human_flavor_cookie.member.entity.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    // 이미 PENDING or ACCEPTED 된 요청이 있는지 확인 -> 중복 요청 방지
    boolean existsByRequesterIdAndReceiverIdAndStatusIn(
            Long requesterId, Long receiverId, List<FriendRequestStatus> statuses
    );
}
