package com.cookie.human_flavor_cookie.cookie.entity;

import com.cookie.human_flavor_cookie.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserCookie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cookie_id", referencedColumnName = "cookie_id", nullable = false) // cookie_id와 매핑
    private Cookie cookie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @Column(nullable = false)
    private boolean isOwned;

    @Column(nullable = false)
    private float accumulatedDistance = 0.0f;
}

