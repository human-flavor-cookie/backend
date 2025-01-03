package com.cookie.human_flavor_cookie.member.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class LoginDto {
    private String email;
    private String password;
}
