package com.cookie.human_flavor_cookie.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Builder
    public Member(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Member update(String name){
        this.name = name;
        return this;
    }
}
