package com.cookie.human_flavor_cookie.cookie.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Setter
public class Cookie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cookie_id")
    private Long cookieId; // Primary Key

    @Column(nullable = false, unique = true)
    private String cookieName; // 쿠키 이름

    @Column(nullable = false)
    private int price;

    public Cookie(String cookieName, int price) {
        this.cookieName = cookieName;
        this.price = price;
    }
}


