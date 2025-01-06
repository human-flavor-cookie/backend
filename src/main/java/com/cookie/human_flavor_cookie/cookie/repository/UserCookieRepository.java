package com.cookie.human_flavor_cookie.cookie.repository;

import com.cookie.human_flavor_cookie.cookie.entity.UserCookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCookieRepository extends JpaRepository<UserCookie, Long> {

    @Query("SELECT uc FROM UserCookie uc WHERE uc.user.id = :userId")
    List<UserCookie> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT uc FROM UserCookie uc WHERE uc.user.id = :userId AND uc.cookie.cookieId = :cookieId")
    Optional<UserCookie> findByUserIdAndCookieId(@Param("userId") Long userId, @Param("cookieId") Long cookieId);

    @Query("SELECT uc FROM UserCookie uc WHERE uc.user.id = :userId AND uc.isOwned = true AND uc.isAlive = true")
    List<UserCookie> findAliveOwnedCookiesByUserId(@Param("userId") Long userId);

}
