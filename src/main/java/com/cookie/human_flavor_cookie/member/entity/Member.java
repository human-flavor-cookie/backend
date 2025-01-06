package com.cookie.human_flavor_cookie.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Entity
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private int coin = 0;

    @Column
    private float target = 1.0f;

    @Column
    private int success = 0;

    @Column
    private int fail = 0;

    @Column
    private float totalKm = 0.0f;

    @Column
    private int totalTime = 0;

    @Column
    private long currentCookie = 0;

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
    public void addCoin(int coins) {
        this.coin += coins;
    }

}
