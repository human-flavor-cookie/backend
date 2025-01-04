package com.cookie.human_flavor_cookie.running.service;

import com.cookie.human_flavor_cookie.exception.CustomException;
import com.cookie.human_flavor_cookie.exception.ErrorCode;
import com.cookie.human_flavor_cookie.member.repository.MemberRepository;
import com.cookie.human_flavor_cookie.running.dto.EndRunResponseDto;
import com.cookie.human_flavor_cookie.running.entity.Running;
import com.cookie.human_flavor_cookie.running.repository.RunningRepository;
import com.cookie.human_flavor_cookie.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class RunningService {
    private final MemberRepository memberRepository;
    private final RunningRepository runningRepository;

    @Transactional
    public EndRunResponseDto endRun(Member member, float distance, int duration) {

        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 오늘 달린 기록 가져오기
        Running running = runningRepository.findByMemberIdAndDate(member.getId(), today)
                .orElse(null);

        if (running == null) {
            // 오늘의 기록이 없으면 새로 생성
            running = Running.builder()
                    .memberId(member.getId())
                    .date(today)
                    .distance(distance)
                    .duration(duration)
                    .build();
        } else {
            // 오늘의 기록이 있으면 업데이트
            running.setDistance(running.getDistance() + distance);
            running.setDuration(running.getDuration() + duration);
        }
        runningRepository.save(running);

        // Member 정보 업데이트
        member.addTotalKm(distance);
        member.addTotalTime(duration);

        // 일일 목표 달성 여부 계산
        boolean dailyTargetMet = running.getDistance() >= member.getTarget();

        if (dailyTargetMet) {
            // 목표 달성: 성공일 증가, 실패일 초기화
            member.setSuccess(member.getSuccess() + 1);
            member.setFail(0);
        } else {
            // 목표 미달성: 실패일 증가, 성공일 초기화
            member.setFail(member.getFail() + 1);
            member.setSuccess(0);
        }

        memberRepository.save(member);
        // 결과 반환
        return EndRunResponseDto.builder()
                .totalDistance(running.getDistance())
                .totalDuration(running.getDuration())
                .dailyTargetMet(dailyTargetMet)
                .build();
    }
}
