package com.cookie.human_flavor_cookie.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnLoginDto {
    private String token;
    private String name;
    private String email;
    private int coin;
    private float target;
    private int success;
    private int fail;
    private float totalKm;
    private int totalTime;
    private long currentCookie;
}
