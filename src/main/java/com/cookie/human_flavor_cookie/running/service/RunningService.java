package com.cookie.human_flavor_cookie.running.service;

import com.cookie.human_flavor_cookie.cookie.entity.UserCookie;
import com.cookie.human_flavor_cookie.cookie.repository.UserCookieRepository;
import com.cookie.human_flavor_cookie.cookie.service.CookieService;
import com.cookie.human_flavor_cookie.exception.CustomException;
import com.cookie.human_flavor_cookie.exception.ErrorCode;
import com.cookie.human_flavor_cookie.member.repository.MemberRepository;
import com.cookie.human_flavor_cookie.running.dto.EndRunResponseDto;
import com.cookie.human_flavor_cookie.running.entity.Running;
import com.cookie.human_flavor_cookie.running.repository.RunningRepository;
import com.cookie.human_flavor_cookie.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class RunningService {
    private final MemberRepository memberRepository;
    private final RunningRepository runningRepository;
    private final UserCookieRepository userCookieRepository;
    private final CookieService cookieService;
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
                    .isGoalMet(false)
                    .build();
        } else {
            // 오늘의 기록이 있으면 업데이트
            running.setDistance(running.getDistance() + distance);
            running.setDuration(running.getDuration() + duration);

        }

        int coinsEarned = (int) (distance * 10000);
        if (!running.isGoalMet() && running.getDistance() >= member.getTarget()) {
            // 추가 코인 계산
            coinsEarned += (int) (member.getTarget() * 5000); // 1km당 5000코인
        }
        runningRepository.save(running);

        // Member 정보 업데이트
        member.addCoin(coinsEarned);
        member.addTotalKm(distance);
        member.addTotalTime(duration);

        if (!running.isGoalMet() && running.getDistance() >= member.getTarget()) {
            running.setGoalMet(true); // 목표 달성 상태 업데이트
            member.setSuccess(member.getSuccess() + 1); // 성공 카운트 증가
            member.setFail(0);
            System.out.println("Goal met! Success count incremented.");
            //천사맛 부활 능력
            boolean ownedAngelCookie = userCookieRepository.findByUserIdAndCookieId(member.getId(), 4L)
                    .map(UserCookie::isOwned)
                    .orElse(false);
            if(member.getSuccess()%7==0&&ownedAngelCookie){
                List<UserCookie> deadCookies = userCookieRepository.findAllByUserId(member.getId()).stream()
                        .filter(UserCookie::isOwned) // 보유 중인 쿠키
                        .filter(userCookie -> !userCookie.isAlive()) // 죽어 있는 쿠키
                        .toList();
                Random random = new Random();
                UserCookie selectedCookie = deadCookies.get(random.nextInt(deadCookies.size()));
                selectedCookie.setAlive(true);
                selectedCookie.setPurchasable(false);
                userCookieRepository.save(selectedCookie);
            }
        }

        // 변경 사항 저장
        runningRepository.save(running);
        memberRepository.save(member);
        Long currentCookieId = member.getCurrentCookie();

        if (currentCookieId != null) {
            userCookieRepository.findByUserIdAndCookieId(member.getId(), currentCookieId)
                    .ifPresent(userCookie -> {
                        userCookie.setAccumulatedDistance(userCookie.getAccumulatedDistance() + distance);
                        userCookieRepository.save(userCookie);
                        System.out.println("Updated distance for cookie ID " + currentCookieId + ": " + userCookie.getAccumulatedDistance());
                    });
        }
        // 구매 가능 쿠키 상태 업데이트
        cookieService.updatePurchasableCookies(member);
        // 버터크림맛쿠키는 보상이 1.2배
        if(currentCookieId == 5L)
        {
            return EndRunResponseDto.builder()
                    .totalDistance(running.getDistance())
                    .totalDuration(running.getDuration())
                    .isGoalMet(running.isGoalMet()) // 목표 달성 여부 전달
                    .coinsEarned((int)(coinsEarned*1.2))
                    .build();
        }
        // DTO 생성 및 반환
        else{
            return EndRunResponseDto.builder()
                    .totalDistance(running.getDistance())
                    .totalDuration(running.getDuration())
                    .isGoalMet(running.isGoalMet()) // 목표 달성 여부 전달
                    .coinsEarned(coinsEarned)
                    .build();
        }
    }
    @Transactional
    public void handleDailyFailures(Member member) {
        // 오늘 날짜와 어제 날짜 가져오기
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 어제의 달린 기록 가져오기
        Running yesterdayRunning = runningRepository.findByMemberIdAndDate(member.getId(), yesterday)
                .orElse(null);

        // 어제 목표를 달성하지 못한 경우 실패 처리
        if (yesterdayRunning == null || !yesterdayRunning.isGoalMet()) {
            member.setFail(member.getFail() + 1); // 실패 카운트 증가
            member.setSuccess(0);
            System.out.println("Fail incremented for member: " + member.getEmail());
            memberRepository.save(member);
        }
        // 실패 후 실패 일수 3일이면 랜덤 쿠키 사망
        cookieService.evaluateFailureAndKillCookie(member);
    }
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    public void evaluateDailyFailures() {
        System.out.println("Starting daily failure evaluation...");
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            handleDailyFailures(member);
        }
        System.out.println("Daily failure evaluation completed.");
    }
}
