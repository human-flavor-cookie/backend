package com.cookie.human_flavor_cookie.running.repository;

import com.cookie.human_flavor_cookie.running.entity.Running;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RunningRepository extends JpaRepository<Running, Long> {
    // 특정 유저(memberId)의 특정 날짜(date)에 해당하는 기록 조회
    Optional<Running> findByMemberIdAndDate(Long memberId, LocalDate date);

}
