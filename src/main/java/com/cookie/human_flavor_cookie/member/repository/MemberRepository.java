package com.cookie.human_flavor_cookie.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cookie.human_flavor_cookie.member.entity.Member;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m ORDER BY m.totalKm DESC")
    List<Member> findAllByOrderByTotalKmDesc();
}


