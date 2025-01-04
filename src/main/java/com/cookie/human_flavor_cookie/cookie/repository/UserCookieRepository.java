package com.cookie.human_flavor_cookie.cookie.repository;

import com.cookie.human_flavor_cookie.cookie.entity.UserCookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCookieRepository extends JpaRepository<UserCookie, Long> {

    @Query("SELECT uc FROM UserCookie uc WHERE uc.user.id = :userId")
    List<UserCookie> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT uc FROM UserCookie uc WHERE uc.user.id = :userId AND uc.cookie.cookieId = :cookieId")
    UserCookie findByUserIdAndCookieId(@Param("userId") Long userId, @Param("cookieId") Long cookieId);
}
