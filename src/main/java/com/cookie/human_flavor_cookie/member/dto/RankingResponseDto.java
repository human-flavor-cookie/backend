package com.cookie.human_flavor_cookie.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingResponseDto {
    private int rank;                 // 순위
    private String userName;          // 유저 이름
    private String currentCookieName; // 장착한 쿠키 이름
    private float totalDistance;      // 총 달린 거리
    private int consecutiveDays;      // 연속 성공/실패 일수
    private boolean isSuccessStreak;  // 연속 성공 여부 (true: 성공, false: 실패)
}

