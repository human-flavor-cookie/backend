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

    @Column
    private int coin;

    @Column
    private float target;

    @Column
    private int success;

    @Column
    private int fail;

    @Column
    private float totalKm;

    @Column
    private int totalTime;

    @Column
    private long currentCookie;

    @Builder
    public Member(String name, String email, String password, int coin, float target,
                  int success, int fail, float totalKm, int totalTime, long currentCookie){
        this.name = name;
        this.email = email;
        this.password = password;
        this.coin = coin;
        this.target = target;
        this.success = success;
        this.fail = fail;
        this.totalKm = totalKm;
        this.totalTime = totalTime;
        this.currentCookie = currentCookie;
    }
    // 닉네임 업데이트
    public Member updateName(String name){
        this.name = name;
        return this;
    }

    // 누적 거리 업데이트
    public void addTotalKm(float distance) {
        if (distance > 0) {
            this.totalKm += distance;
        }
    }

    // 누적 시간 업데이트
    public void addTotalTime(int duration) {
        if (duration > 0) {
            this.totalTime += duration;
        }
    }

    // 일일 목표 설정 (필요하면 추가)
    public void setTarget(float target) {
        if (target > 0) {
            this.target = target;
        }
    }

}
