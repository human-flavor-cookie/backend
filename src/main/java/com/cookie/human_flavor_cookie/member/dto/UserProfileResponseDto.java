package com.cookie.human_flavor_cookie.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponseDto {
    private int consecutiveSuccessDays; // 며칠 연속 성공
    private int consecutiveFailDays;    // 며칠 연속 실패
    private float dailyGoal;            // 일일 목표 (km)
    private float totalDistance;        // 총 달린 거리 (km)
    private String averagePace;         // 평균 페이스 (분:초 형식)
}
