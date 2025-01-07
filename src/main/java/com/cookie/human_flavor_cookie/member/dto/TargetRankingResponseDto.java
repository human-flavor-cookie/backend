package com.cookie.human_flavor_cookie.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TargetRankingResponseDto {
    private int targetRank;               // 랭킹
    private String userName;      // 유저 이름
    private Long currentCookieId;
    private float dailyDistance;      // 총 달린 거리
    private int consecutiveDays;      // 연속 성공/실패 일수
    private boolean isSuccessStreak;
    private float currentTier;
}
