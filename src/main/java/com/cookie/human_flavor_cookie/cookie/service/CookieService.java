package com.cookie.human_flavor_cookie.cookie.service;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.cookie.dto.UserCookieResponseDto;
import com.cookie.human_flavor_cookie.cookie.entity.Cookie;
import com.cookie.human_flavor_cookie.cookie.entity.UserCookie;
import com.cookie.human_flavor_cookie.cookie.repository.CookieRepository;
import com.cookie.human_flavor_cookie.cookie.repository.UserCookieRepository;
import com.cookie.human_flavor_cookie.exception.CustomException;
import com.cookie.human_flavor_cookie.exception.ErrorCode;
import com.cookie.human_flavor_cookie.member.entity.Member;
import com.cookie.human_flavor_cookie.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CookieService {
    private final CookieRepository cookieRepository;
    private final UserCookieRepository userCookieRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void assignCookieToUser(Member user, Long cookieId) {
        // UserCookie 엔티티를 검색
        UserCookie userCookie = userCookieRepository.findByUserIdAndCookieId(user.getId(), cookieId)
                .orElseThrow(() -> new RuntimeException("Cookie not found for user."));
        // 이미 보유한 쿠키인지 확인
        if (userCookie.isOwned()) {
            throw new RuntimeException("User already owns this cookie.");
        }
        // 보유 상태 업데이트
        userCookie.setOwned(true);
        userCookieRepository.save(userCookie);

        System.out.println("Cookie with ID " + cookieId + " assigned to user: " + user.getEmail());
    }

    @Transactional(readOnly = true)
    public List<UserCookie> getCookiesForUser(Long userId) {
        return userCookieRepository.findAllByUserId(userId);
    }

    @Transactional
    public void updateCookieDistance(Long userId, Long cookieId, float distance) {
        userCookieRepository.findByUserIdAndCookieId(userId, cookieId)
                .ifPresentOrElse(userCookie -> {
                    userCookie.setAccumulatedDistance(userCookie.getAccumulatedDistance() + distance);
                    userCookieRepository.save(userCookie);
                }, () -> {
                    throw new RuntimeException("User does not own this cookie");
                });
    }

    @Transactional
    public void changeCurrentCookie(Member user, Long cookieId) {
        // 사용자가 보유한 쿠키인지 확인
        userCookieRepository.findByUserIdAndCookieId(user.getId(), cookieId)
                .ifPresentOrElse(userCookie -> {
                    if (!userCookie.isOwned()) {
                        throw new RuntimeException("User does not own the specified cookie.");
                    }
                    // Member의 currentCookie 업데이트
                    user.setCurrentCookie(cookieId);
                    memberRepository.save(user);
                }, () -> {
                    throw new RuntimeException("User does not own the specified cookie.");
                });
    }
    @Transactional
    public void initializeUserCookies(Member user) {
        List<Cookie> cookies = cookieRepository.findAll();
        for (Cookie cookie : cookies) {
            // UserCookie가 이미 존재하면 건너뜀
            if (userCookieRepository.findByUserIdAndCookieId(user.getId(), cookie.getCookieId()).isPresent()) {
                continue;
            }

            // UserCookie 생성
            UserCookie userCookie = new UserCookie();
            userCookie.setUser(user);
            userCookie.setCookie(cookie);
            userCookie.setOwned(false); // 초기값: 보유하지 않음
            userCookie.setPurchasable(false); // 초기값: 구매 불가
            userCookie.setAccumulatedDistance(0.0f);

            // 특정 쿠키 조건 설정
            if (cookie.getCookieId() == 1L) {
                userCookie.setOwned(true);
            }
            if (cookie.getCookieId() == 3L) {
                userCookie.setPurchasable(true);
            }

            userCookieRepository.save(userCookie);
        }
    }

    @Transactional
    public void purchaseCookie(Member user, Long cookieId) {
        UserCookie userCookie = userCookieRepository.findByUserIdAndCookieId(user.getId(), cookieId)
                .orElseThrow(() -> new RuntimeException("Cookie not found for user."));

        if (userCookie.isOwned()) {
            throw new RuntimeException("User already owns this cookie.");
        }

        if (!userCookie.isPurchasable()) {
            throw new RuntimeException("This cookie is not available for purchase.");
        }
        // 쿠키 가격 확인
        Cookie cookie = userCookie.getCookie();
        int cookiePrice = cookie.getPrice(); // 쿠키 엔티티에 가격 필드 추가
        if (user.getCoin() < cookiePrice) {
            throw new CustomException(ErrorCode.NO_MONEY);
        }
        // 쿠키 구매 처리
        user.setCoin(user.getCoin() - cookiePrice);
        userCookie.setOwned(true);
        userCookie.setPurchasable(false); // 구매 후 더 이상 구매 불가
        userCookieRepository.save(userCookie);
        memberRepository.save(user);
    }
    @Transactional
    public void updatePurchasableCookies(Member user) {
        List<UserCookie> userCookies = userCookieRepository.findAllByUserId(user.getId());
        for (UserCookie userCookie : userCookies) {
            Cookie cookie = userCookie.getCookie();
            //좀비맛쿠키: 1시간 뛰면 구매 가능
            if (user.getTotalTime() >= 3600 && userCookie.getId() == 2L && !userCookie.isOwned()) {
                userCookie.setPurchasable(true);
            }
            //명랑한쿠키: 조건 없이 구매 가능
            if (userCookie.getId() == 3L && !userCookie.isOwned()) userCookie.setPurchasable(true);
            //천사맛쿠키: 7일 연속 성공시 구매 가능
            if (user.getSuccess() >= 7 && userCookie.getId() == 4L && !userCookie.isOwned()) { // 7일 연속 성공
                userCookie.setPurchasable(true);
            }
            //버터크림맛쿠키: 누적 거리 10km 구매 가능
            if (user.getTotalKm() >= 10.0f && userCookie.getId() == 5L && !userCookie.isOwned()) {
                userCookie.setPurchasable(true);
            }
            userCookieRepository.save(userCookie);
        }
    }
    @Transactional(readOnly = true)
    public List<UserCookieResponseDto> getUserCookies(Member user) {
        // 유저의 모든 쿠키 조회
        List<UserCookie> userCookies = userCookieRepository.findAllByUserId(user.getId());

        // 엔티티를 DTO로 변환
        return userCookies.stream()
                .map(userCookie -> UserCookieResponseDto.builder()
                        .cookieId(userCookie.getCookie().getCookieId())
                        .cookieName(userCookie.getCookie().getCookieName())
                        .isOwned(userCookie.isOwned())
                        .isPurchasable(userCookie.isPurchasable())
                        .accumulatedDistance(userCookie.getAccumulatedDistance())
                        .build())
                .toList();
    }

}
