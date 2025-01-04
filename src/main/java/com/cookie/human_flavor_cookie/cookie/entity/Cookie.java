package com.cookie.human_flavor_cookie.cookie.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Cookie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cookie_id")
    private Long cookieId; // Primary Key

    @Column(nullable = false, unique = true)
    private String cookieName; // 쿠키 이름

    // 이름을 설정하는 생성자 추가
    public Cookie(String cookieName) {
        this.cookieName = cookieName;
    }
}


