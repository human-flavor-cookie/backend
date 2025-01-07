package com.cookie.human_flavor_cookie.member.service;

import com.cookie.human_flavor_cookie.auth.jwt.JwtTokenProvider;
import com.cookie.human_flavor_cookie.cookie.entity.Cookie;
import com.cookie.human_flavor_cookie.cookie.entity.UserCookie;
import com.cookie.human_flavor_cookie.cookie.repository.CookieRepository;
import com.cookie.human_flavor_cookie.cookie.repository.UserCookieRepository;
import com.cookie.human_flavor_cookie.cookie.service.CookieService;
import com.cookie.human_flavor_cookie.exception.CustomException;
import com.cookie.human_flavor_cookie.exception.ErrorCode;
import com.cookie.human_flavor_cookie.member.dto.LoginDto;
import com.cookie.human_flavor_cookie.member.dto.MainPageDto;
import com.cookie.human_flavor_cookie.member.dto.ReturnLoginDto;
import com.cookie.human_flavor_cookie.member.dto.SignupDto;
import com.cookie.human_flavor_cookie.member.dto.*;
import com.cookie.human_flavor_cookie.member.repository.MemberRepository;

import com.cookie.human_flavor_cookie.running.entity.Running;
import com.cookie.human_flavor_cookie.running.repository.RunningRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cookie.human_flavor_cookie.member.entity.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieRepository cookieRepository;
    private final UserCookieRepository userCookieRepository;
    private final RunningRepository runningRepository;
    private final CookieService cookieService;
    @Transactional
    public void signup(SignupDto signupDto) throws Exception {
        //이미 회원이 존재하는 경우
        if (memberRepository.findByEmail(signupDto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        signupDto.encodingPassword(passwordEncoder);

        Member member = Member.builder()
                .email(signupDto.getEmail())
                .name(signupDto.getName())
                .password(signupDto.getPassword())
                .target(0.0f)
                .currentCookie(1L)
                .build();
        memberRepository.save(member);
        cookieService.initializeUserCookies(member);
    }
    @Transactional
    public ReturnLoginDto login(LoginDto loginDto) throws Exception {

        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_MEMBER));

        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
        }

        // JWT 토큰 생성
        String jwt = jwtTokenProvider.createToken(member.getEmail());

        return new ReturnLoginDto(
                jwt,
                member.getName(),
                member.getEmail(),
                member.getCoin(),
                member.getTarget(),
                member.getSuccess(),
                member.getFail(),
                member.getTotalKm(),
                member.getTotalTime(),
                member.getCurrentCookie()
        );
    }

    public void logout(Member member) {
        httpSession.removeAttribute("member");
        httpSession.invalidate();
    }

    @Transactional
    public void updateDailyTarget(Member member, float newTarget) {
        if (newTarget <= 0) {
            throw new IllegalArgumentException("Target must be greater than 0.");
        }
        member.setTarget(newTarget); // 목표 값 업데이트
        memberRepository.save(member); // 변경 내용 저장
    }
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email); // 중복이 없으면 true 반환
    }
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(Member member) {
        String userName = member.getName();
        // 연속 성공/실패 정보 가져오기
        int consecutiveSuccessDays = member.getSuccess();
        int consecutiveFailDays = member.getFail();

        // 일일 목표
        float dailyGoal = member.getTarget();

        // 총 달린 거리
        float totalDistance = member.getTotalKm();

        // 평균 페이스 계산
        int totalTime = member.getTotalTime();
        String averagePace = calculateAveragePace(totalDistance, totalTime);

        // DTO 생성 및 반환
        return UserProfileResponseDto.builder()
                .userName(userName)
                .consecutiveSuccessDays(consecutiveSuccessDays)
                .consecutiveFailDays(consecutiveFailDays)
                .dailyGoal(dailyGoal)
                .totalDistance(totalDistance)
                .averagePace(averagePace)
                .build();
    }

    // 평균 페이스 계산 (분:초 형식)
    private String calculateAveragePace(float totalDistance, int totalTime) {
        if (totalDistance <= 0) {
            return "0:00"; // 거리가 없으면 0분 0초 반환
        }
        int paceInSeconds = (int) (totalTime / totalDistance);
        int minutes = paceInSeconds / 60;
        int seconds = paceInSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    @Transactional(readOnly = true)
    public List<RankingResponseDto> getRanking(Member currentUser) {
        List<Member> members = memberRepository.findAllByOrderByTotalKmDesc();
        List<RankingResponseDto> ranking = new ArrayList<>();
        int userRank = 0;

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            int rank = i + 1;

            // 현재 유저의 랭킹 저장
            if (member.getId().equals(currentUser.getId())) {
                userRank = rank;
            }

            // 장착한 쿠키 이름 가져오기
            String currentCookieName = "";
            Long currentCookieId = member.getCurrentCookie(); // Long으로 선언

            // 연속 성공 또는 실패 일수 계산
            int consecutiveDays = member.getSuccess() > 0 ? member.getSuccess() : member.getFail();
            boolean isSuccessStreak = member.getSuccess() > 0;

            // 랭킹 DTO 생성
            ranking.add(RankingResponseDto.builder()
                    .rank(rank)
                    .userName(member.getName())
                    .currentCookieId(currentCookieId)
                    .totalDistance(member.getTotalKm())
                    .consecutiveDays(consecutiveDays)
                    .isSuccessStreak(isSuccessStreak)
                    .build());
        }

        return ranking;
    }
    //이름, 코인, 오늘 달린 거리, 목표 반환
    @Transactional(readOnly = true)
    public MainPageDto getMainPage(Member member) {
        LocalDate today = LocalDate.now();
        double distanceToday = runningRepository.findByMemberIdAndDate(member.getId(), today)
                .map(Running::getDistance) // Running이 존재하면 거리 반환
                .orElse(0.0f);
        double goalDistance = member.getTarget();
        return new MainPageDto(member.getName(), member.getCoin(), distanceToday, goalDistance, member.getCurrentCookie());
    }
    @Transactional(readOnly = true)
    public List<DailyRankingResponseDto> getDailyRanking(Member currentUser) {
        List<Member> members = memberRepository.findAll();
        List<DailyRankingResponseDto> dailyRanking = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 1. 각 멤버별 오늘의 거리 가져오기 및 DTO 생성
        for (Member member : members) {
            // 오늘 달린 거리 가져오기
            float dailyDistance = runningRepository.findByMemberIdAndDate(member.getId(), today)
                    .map(Running::getDistance)
                    .orElse(0f);

            // 연속 성공 또는 실패 일수 계산
            int consecutiveDays = member.getSuccess() > 0 ? member.getSuccess() : member.getFail();
            boolean isSuccessStreak = member.getSuccess() > 0;

            // DTO 생성 (일단 랭킹은 나중에 할당)
            dailyRanking.add(DailyRankingResponseDto.builder()
                    .dailyRank(0) // 초기값은 0
                    .userName(member.getName())
                    .currentCookieId(member.getCurrentCookie())
                    .dailyDistance(dailyDistance)
                    .consecutiveDays(consecutiveDays)
                    .isSuccessStreak(isSuccessStreak)
                    .build());
        }

        // 2. dailyDistance 기준 내림차순 정렬
        dailyRanking.sort((a, b) -> Float.compare(b.getDailyDistance(), a.getDailyDistance()));

        // 3. 정렬된 리스트를 기준으로 랭킹 번호 재할당
        int rank = 1;
        for (DailyRankingResponseDto dto : dailyRanking) {
            dto.setDailyRank(rank++);
        }
        return dailyRanking;
    }
    @Transactional(readOnly = true)
    public List<TargetRankingResponseDto> getRankingByTier(float minTarget, float maxTarget) {
        // 해당 티어의 유저 조회
        List<Member> tierMembers = memberRepository.findMembersByTier(minTarget, maxTarget);

        // 랭킹 계산
        int rank = 1;
        List<TargetRankingResponseDto> rankings = new ArrayList<>();
        for (Member member : tierMembers) {
            rankings.add(TargetRankingResponseDto.builder()
                    .rank(rank++)
                    .memberName(member.getName())
                    .totalKm(member.getTotalKm())
                    .successDays(member.getSuccess())
                    .currentCookieId(member.getCurrentCookie())
                    .build());
        }

        return rankings;
    }

}

