package com.cookie.human_flavor_cookie.cookie.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCookieResponseDto {
    private Long cookieId; // 쿠키 ID
    private String cookieName; // 쿠키 이름
    private boolean isOwned; // 보유 여부
    private float accumulatedDistance; // 누적 거리
}
