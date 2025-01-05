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
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cookie.human_flavor_cookie.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieRepository cookieRepository;
    private final UserCookieRepository userCookieRepository;
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

    @Transactional(readOnly = true)
    public MainPageDto getMainPage(Member member) {
        return new MainPageDto(member.getName(), member.getCoin());
            if (currentCookieId != null) { // null 체크
                currentCookieName = cookieRepository.findById(currentCookieId)
                        .map(Cookie::getCookieName)
                        .orElse("");
            }

            // 연속 성공 또는 실패 일수 계산
            int consecutiveDays = member.getSuccess() > 0 ? member.getSuccess() : member.getFail();
            boolean isSuccessStreak = member.getSuccess() > 0;

            // 랭킹 DTO 생성
            ranking.add(RankingResponseDto.builder()
                    .rank(rank)
                    .userName(member.getName())
                    .currentCookieName(currentCookieName)
                    .totalDistance(member.getTotalKm())
                    .consecutiveDays(consecutiveDays)
                    .isSuccessStreak(isSuccessStreak)
                    .build());
        }

        return ranking;
    }
}

