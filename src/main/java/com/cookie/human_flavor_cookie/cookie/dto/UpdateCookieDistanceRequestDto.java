package com.cookie.human_flavor_cookie.cookie.dto;

import lombok.Data;

@Data
public class UpdateCookieDistanceRequestDto {
    private Long cookieId; // 업데이트할 쿠키 ID
    private float distance; // 추가된 거리
}
