package com.cookie.human_flavor_cookie.cookie.service;

import com.cookie.human_flavor_cookie.auth.LoginUser;
import com.cookie.human_flavor_cookie.cookie.entity.Cookie;
import com.cookie.human_flavor_cookie.cookie.entity.UserCookie;
import com.cookie.human_flavor_cookie.cookie.repository.CookieRepository;
import com.cookie.human_flavor_cookie.cookie.repository.UserCookieRepository;
import com.cookie.human_flavor_cookie.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CookieService {
    private final CookieRepository cookieRepository;
    private final UserCookieRepository userCookieRepository;

    @Transactional
    public void assignCookieToUser(Member user, Long cookieId) {

        Cookie cookie = cookieRepository.findById(cookieId)
                .orElseThrow(() -> new RuntimeException("Cookie not found"));

        UserCookie userCookie = new UserCookie();
        userCookie.setUser(user);
        userCookie.setCookie(cookie);
        userCookie.setOwned(true);
        userCookie.setAccumulatedDistance(0.0f);

        userCookieRepository.save(userCookie);
    }

    @Transactional(readOnly = true)
    public List<UserCookie> getCookiesForUser(Long userId) {
        return userCookieRepository.findAllByUserId(userId);
    }

    @Transactional
    public void updateCookieDistance(Long userId, Long cookieId, float distance) {
        UserCookie userCookie = userCookieRepository.findByUserIdAndCookieId(userId, cookieId);
        if (userCookie == null) {
            throw new RuntimeException("User does not own this cookie");
        }
        userCookie.setAccumulatedDistance(userCookie.getAccumulatedDistance() + distance);
        userCookieRepository.save(userCookie);
    }
}

