package com.cookie.human_flavor_cookie.running.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EndRunResponseDto {
    private float totalDistance; // 오늘의 총 거리
    private int totalDuration; // 오늘의 총 시간
    private boolean isGoalMet; // 일일 목표 달성 여부
    private int coinsEarned;
}
