package com.cookie.human_flavor_cookie.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainPageDto {
    private String name;
    private int coin;
    private double distanceToday;
    private double goalDistance;
    private Long currentCookieId;
    private int pendingCount;
}