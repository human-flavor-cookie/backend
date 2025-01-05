package com.cookie.human_flavor_cookie.member.service;

import com.cookie.human_flavor_cookie.auth.jwt.JwtTokenProvider;
import com.cookie.human_flavor_cookie.cookie.entity.Cookie;
import com.cookie.human_flavor_cookie.cookie.entity.UserCookie;
import com.cookie.human_flavor_cookie.cookie.repository.CookieRepository;
import com.cookie.human_flavor_cookie.cookie.repository.UserCookieRepository;
import com.cookie.human_flavor_cookie.exception.CustomException;
import com.cookie.human_flavor_cookie.exception.ErrorCode;
import com.cookie.human_flavor_cookie.member.dto.LoginDto;
import com.cookie.human_flavor_cookie.member.dto.MainPageDto;
import com.cookie.human_flavor_cookie.member.dto.ReturnLoginDto;
import com.cookie.human_flavor_cookie.member.dto.SignupDto;
import com.cookie.human_flavor_cookie.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cookie.human_flavor_cookie.member.entity.Member;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieRepository cookieRepository;
    private final UserCookieRepository userCookieRepository;
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
        addDefaultCookieToUser(member);
    }
    private void addDefaultCookieToUser(Member member) {
        // 쿠키 엔티티(ID 1) 조회 또는 생성
        Cookie defaultCookie = cookieRepository.findById(1L).orElseGet(() -> {
            Cookie cookie = new Cookie();
            cookie.setCookieId(1L);
            cookie.setCookieName("Default Cookie");
            return cookieRepository.save(cookie);
        });

        // 사용자에게 ID 1 쿠키 추가
        UserCookie userCookie = new UserCookie();
        userCookie.setUser(member);
        userCookie.setCookie(defaultCookie);
        userCookie.setOwned(true);
        userCookie.setAccumulatedDistance(0.0f);
        userCookieRepository.save(userCookie);
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
    public MainPageDto getMainPage(Member member) {
        return new MainPageDto(member.getName(), member.getCoin());
    }
}

