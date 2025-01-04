package com.cookie.human_flavor_cookie.cookie.repository;

import com.cookie.human_flavor_cookie.cookie.entity.Cookie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CookieRepository extends JpaRepository<Cookie, Long> {
}
