package com.cookie.human_flavor_cookie.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TargetRankingResponseDto {
    private int rank;               // 랭킹
    private String memberName;      // 유저 이름
    private float totalKm;          // 누적 달린 거리
    private int successDays;        // 연속 성공 일수
    private long currentCookieId; // 장착한 쿠키 이름
}
