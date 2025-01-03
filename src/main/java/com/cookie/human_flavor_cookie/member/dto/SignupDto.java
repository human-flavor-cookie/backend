package com.cookie.human_flavor_cookie.member.dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@Getter
public class SignupDto {
    private String name;
    private String email;
    private String password;

    public void encodingPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
