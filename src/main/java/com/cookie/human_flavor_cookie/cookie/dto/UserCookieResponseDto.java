package com.cookie.human_flavor_cookie.cookie.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCookieResponseDto {
    private Long cookieId; // 쿠키 ID
    private String cookieName; // 쿠키 이름
    private boolean isOwned; // 보유 여부
    private boolean isPurchasable; // 구매 가능 여부
    private float accumulatedDistance; // 함께 달린 거리
    private boolean isEquipped; // 쿠키 장착 여부
    private int cookiePrice; // 쿠키 가격
    private boolean isAlive; // 쿠키 생존 여부
}
