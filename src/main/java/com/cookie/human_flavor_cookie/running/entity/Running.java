package com.cookie.human_flavor_cookie.running.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Entity
@Setter
public class Running {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 달리기 세션 ID (Primary Key)

    @Column(nullable = false)
    private Long memberId; // 회원 ID (Foreign Key, Member와 연관)

    @Column(nullable = false)
    private LocalDate date; // 날짜

    @Column(nullable = false)
    private float distance; // 하루 동안 달린 거리 (예: 5.0km)

    @Column(nullable = false)
    private int duration; // 하루 동안 달린 시간 (초 단위)

    @Column(nullable = false)
    private boolean isGoalMet = false; // 오늘 목표 달성 여부

    @Builder
    public Running(Long memberId, LocalDate date, float distance, int duration, boolean isGoalMet) {
        this.memberId = memberId;
        this.date = date;
        this.distance = distance;
        this.duration = duration;
        this.isGoalMet = isGoalMet;
    }
}

