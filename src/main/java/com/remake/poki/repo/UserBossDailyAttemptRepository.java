package com.remake.poki.repo;

import com.remake.poki.model.UserBossDailyAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
@Repository
public interface UserBossDailyAttemptRepository extends JpaRepository<UserBossDailyAttempt, Long> {
    Optional<UserBossDailyAttempt> findByUserIdAndBossScheduleIdAndAttemptDate(
            Long userId, Long bossScheduleId, LocalDate attemptDate);
    void deleteByAttemptDateBefore(LocalDate date);
}